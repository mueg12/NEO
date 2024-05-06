package com.neo.back.docker.service;

import com.neo.back.docker.dto.GameServerSettingDto;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.entity.MinecreftServerSetting;
import com.neo.back.docker.middleware.DockerAPI;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.GameServerSettingRepository;
import com.neo.back.docker.utility.MakeWebClient;

import com.neo.back.springjwt.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServerSettingService {

    private final GameServerSettingRepository gameServerSettingRepo;
    private final DockerServerRepository dockerServerRepo;
    private final MakeWebClient makeWebClient;
    private final DockerAPI dockerAPI;
    private WebClient dockerWebClient;

    public Mono<String> getServerSetting(User user) {

        DockerServer dockerServer = dockerServerRepo.findByUser(user);
        if (dockerServer == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not have an open server."));
        }
        this.dockerWebClient =  this.makeWebClient.makeDockerWebClient(dockerServer.getEdgeServer().getIp());
        String containerId = dockerServer.getDockerId();
        String filePathInContainer = dockerServer.getGame().getDefaultSetting().getSettingFilePath();
        Path localPath = Path.of("/mnt/nas/serverSetting/" + /*user +*/ ".tar");

        // Docker 컨테이너로부터 파일 받아오기
        return this.getDockerContainerFile(containerId, filePathInContainer, localPath)
                .flatMap(response -> this.settingFormatConversion(localPath));

    }

    public Mono<String> setServerSetting(GameServerSettingDto req) throws IOException {

        DockerServer dockerServer = dockerServerRepo.findByUser(null);
        if (dockerServer == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not have an open server."));
        }
        this.dockerWebClient =  this.makeWebClient.makeDockerWebClient(dockerServer.getEdgeServer().getIp());

        System.out.println(req.toString());

        String dockerContainerId= req.getContainerId();
        Long UserId = req.getUserId();

        System.out.println(UserId);
        System.out.println(dockerContainerId);

        // 추후 jwt 토큰에 User 정보 담아둘 거임. 임시 코드

        GameServerSetting settings = this.loadSettings(UserId); // 서비스 메소드는 적절한 로직으로 구현되어야 함

        // 모든 필드와 값을 가져와서 "컬럼: 값" 형식의 문자열로 변환
        String content = this.getString(settings);

        System.out.println(content);

        // content 문자열을 바이트 배열로 변환
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // 파일 내용을 tar 파일로 압축
        byte[] tarFile = this.createTarContent(contentBytes);

        // tar 파일을 저장할 경로
        Path tarPath = Path.of("server.properties.tar");

        // tar 파일 바이트 배열을 실제 파일로 저장
        Files.write(tarPath, tarFile);

        // Docker API를 통해 파일을 컨테이너에 복사
        return this.changeFileinContainer(dockerContainerId, tarFile);
    }



    @SuppressWarnings("deprecation")
    private Mono<String> getDockerContainerFile(String containerId, String filePathInContainer, Path localPath) {
        return this.dockerAPI.downloadFile(containerId, filePathInContainer, this.dockerWebClient)
                .flatMap(dataBuffer -> {
                    System.out.println();
                    // 받아온 tar 파일을 로컬에 저장
                    try (WritableByteChannel channel = Files.newByteChannel(localPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                        // DataBuffer에서 데이터를 읽어 로컬 파일에 쓰기
                        channel.write(dataBuffer.asByteBuffer());
                        return Mono.just("File received and saved as " + localPath.toString());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }

    private Mono<String> settingFormatConversion(Path localPath) {
        try {
            String propertiesString  = this.extractPropertiesFromTar(localPath.toString());

            JSONObject json = new JSONObject();
            String[] lines = propertiesString .split("\n");

            for (String line : lines) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    String[] keyValue = line.split("=", 2);
                    if (keyValue.length == 2) {
                        json.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            return Mono.just(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Mono.just("Error extracting server.properties");
        }
    }

    private String extractPropertiesFromTar(String localPath) throws IOException {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(localPath))) {
            tarInput.getNextTarEntry(); // server.properties 파일로 바로 이동
            ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tarInput.read(buffer)) > 0) {
                contentBuffer.write(buffer, 0, len);
            }
            return contentBuffer.toString(StandardCharsets.UTF_8.name());
        }
    }


    private GameServerSetting loadSettings(Long UserId) {
        // 처음 데이터 반환하게 함.
        // 더미 데이터 일단은 넣어두고 테스트
        Optional<GameServerSetting> settings = gameServerSettingRepo.findById(UserId);

        GameServerSetting returnsettings = settings.orElse(new MinecreftServerSetting()); // 게임별로 상황나눠야함

        return returnsettings;
    }

    private String getString(GameServerSetting settings) {


        String content = Arrays.stream(GameServerSetting.class.getDeclaredFields())
                .map(field -> this.formatField(field, (MinecreftServerSetting) settings)) //게임별로 상황 나눠야함
                .collect(Collectors.joining("\n"));
        return content;
    }

    private byte[] createTarContent(byte[] fileContent) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(out)) {

            // TarArchiveEntry 설정 (파일 이름은 임의로 지정)
            TarArchiveEntry entry = new TarArchiveEntry("server.properties");
            entry.setSize(fileContent.length); // 파일 크기 설정
            tarOut.putArchiveEntry(entry);

            // 파일 내용 쓰기
            tarOut.write(fileContent);
            tarOut.closeArchiveEntry();

            // tarOut을 닫아야 tar 파일 완성
            tarOut.finish();

            // 완성된 tar 파일의 바이트 배열 반환
            return out.toByteArray();
        }
    }

    private Mono<String> changeFileinContainer(String containerId, byte[] tarFile) {
        return  this.dockerAPI.uploadFile(containerId, "/server", tarFile, this.dockerWebClient)
                .then(this.dockerAPI.restartContainer(containerId, this.dockerWebClient))
                .thenReturn("File updated and container restarted.");
    }

    private String formatField(Field field, GameServerSetting settings) {
        try {
            field.setAccessible(true); // private 필드에 접근할 수 있도록 설정
            String fieldName = field.getName().replace('_', '-');

            Object value = field.get(settings);
            return fieldName + ":" + value;
        } catch (IllegalAccessException e) {
            return field.getName() + ": error accessing value";
        }
    }





    public Mono<String> executeCommand(String containerId, String command) {
        DockerServer dockerServer = dockerServerRepo.findByUser(null);
        if (dockerServer == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not have an open server."));
        }
        this.dockerWebClient = this.makeWebClient.makeDockerWebClient(dockerServer.getEdgeServer().getIp());
        
        var createExecRequest = Map.of(
            "AttachStdout", true,
            "AttachStderr", true,
            "Cmd", List.of("/bin/sh", "-c", command)
        );

        var startExecRequest = Map.of("Detach", false, "Tty", false);

        return this.dockerAPI.makeExec(containerId, createExecRequest, this.dockerWebClient)
                .flatMap(execCreationResponse -> {
                    String execId = (String) execCreationResponse.get("Id");

                    // Exec 시작 및 결과 수집
                    return this.dockerAPI.startExec(execId, startExecRequest, this.dockerWebClient);
                });
    }

    public Mono<String> getContainerStats(String containerId) {
        return this.dockerAPI.getContainerInfo(containerId, this.dockerWebClient);
    }


}