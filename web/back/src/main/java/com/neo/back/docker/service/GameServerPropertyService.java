package com.neo.back.docker.service;

import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.entity.MinecreftServerSetting;
import com.neo.back.docker.middleware.DockerAPI;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.repository.MinecreftServerSettingRepository;
import com.neo.back.docker.utility.MakeWebClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
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
public class GameServerPropertyService {

    private final MinecreftServerSettingRepository MinecreftServerSettingrepo;
    private final DockerServerRepository dockerServerRepo;
    private final MakeWebClient makeWebClient;
    private final DockerAPI dockerAPI;
    private WebClient dockerWebClient;

    public MinecreftServerSetting save(MinecreftServerSetting property) {
        return MinecreftServerSettingrepo.save(property);
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

    public Mono<String> ChangeFileinContainer(String containerId, byte[] tarFile) {
        return dockerWebClient.put()
                .uri("/containers/" + containerId + "/archive?path=/server")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(tarFile)
                .retrieve()
                .bodyToMono(Void.class)  // 요청 성공시 응답 본문은 없음
                .then(restartContainer(containerId))
                .thenReturn("File updated and container restarted.");
    }



    private  Mono<Void> restartContainer(String containerId) {
        return dockerWebClient.post()
                .uri("/containers/" + containerId + "/restart")
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<String> getContainerStats(String containerId) {
        return this.dockerWebClient
                .get()
                .uri("/containers/{containerId}/stats?stream=false", containerId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public MinecreftServerSetting loadSettings(Long UserId) {
        // 처음 데이터 반환하게 함.
        // 더미 데이터 일단은 넣어두고 테스트
        Optional<MinecreftServerSetting> settings = MinecreftServerSettingrepo.findById(UserId);

        MinecreftServerSetting returnsettings = settings.orElse(new MinecreftServerSetting());

        return returnsettings;
    }

    public String getString(MinecreftServerSetting settings) {


        String content = Arrays.stream(MinecreftServerSetting.class.getDeclaredFields())
                .map(field -> formatField(field, settings))
                .collect(Collectors.joining("\n"));
        return content;
    }
    public  Mono<String> getDockerContainerFile(String containerId, String filePathInContainer) {
        return dockerWebClient.get()
                .uri("/containers/" + containerId + "/archive?path=" + filePathInContainer)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(DataBuffer.class)  // DataBuffer로 응답 바디 받기
                .flatMap(dataBuffer -> {
                    // 받아온 tar 파일을 로컬에 저장
                    Path localTarPath = Path.of("server.properties.tar");
                    try (WritableByteChannel channel = Files.newByteChannel(localTarPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                        // DataBuffer에서 데이터를 읽어 로컬 파일에 쓰기
                        channel.write(dataBuffer.asByteBuffer());
                        return Mono.just("File received and saved as " + localTarPath.toString());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }
    public String extractPropertiesFromTar(String tarFilePath) throws IOException {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(tarFilePath))) {
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

    private String formatField(Field field, MinecreftServerSetting settings) {
        try {
            field.setAccessible(true); // private 필드에 접근할 수 있도록 설정
            String fieldName = field.getName().replace('_', '-');

            Object value = field.get(settings);
            return fieldName + ":" + value;
        } catch (IllegalAccessException e) {
            return field.getName() + ": error accessing value";
        }
    }

}