package com.neo.back.docker.service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.middleware.DockerAPI;
import com.neo.back.docker.repository.DockerServerRepository;
import com.neo.back.docker.utility.MakeWebClient;
import com.neo.back.springjwt.entity.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UploadAndDownloadService {

    private final DockerServerRepository dockerServerRepo;
    private final MakeWebClient makeWebClient;
    private final DockerAPI dockerAPI;
    private WebClient dockerWebClient;

    public Mono<String> upload(MultipartFile[] files, String path){
        User user = null;
        String userId = "null"; 
        DockerServer dockerServer = dockerServerRepo.findByUser(user);
        String ip = dockerServer.getEdgeServer().getIp();
        String dockerId = dockerServer.getDockerId();
        this.dockerWebClient = makeWebClient.makeDockerWebClient(ip);
        Path basePath = Paths.get("").toAbsolutePath().resolve("src/main/resources/test/"+userId);
        Path tarPath = Paths.get("").toAbsolutePath().resolve("src/main/resources/test/"+userId+"/userTar.tar");
        byte[] tarFileBytes = null;
        
        saveUserFolder_file(files,basePath);
        createTarArchive(basePath,tarPath);

        try {
            tarFileBytes = Files.readAllBytes(tarPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mono<String> result = postUserTarToContainer(dockerId, tarFileBytes, path);
        String[] delMeoStr = {"rm","-f","server/userTar.tar"};
        Map<String, Object> delMesList = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", delMeoStr,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );

        Map<String, Boolean> delStartList = Map.of(
            "Detach", false,
            "Tty", true
        );
        result.block();
        Mono<Map> execIdMes = this.dockerAPI.makeExec(dockerId, delMesList, this.dockerWebClient);
        String execId = (String) execIdMes.block().get("Id");
        this.dockerAPI.startExec( execId, delStartList, this.dockerWebClient);
        delFileAndFolderAndTar(basePath);
        return  result; 
    }

    private void delFileAndFolderAndTar(Path basePath) {
        try {
            Files.walkFileTree(basePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // 파일 삭제
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // 디렉터리 삭제
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("폴더 및 파일 삭제 완료.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Mono<String> postUserTarToContainer(String containerId, byte[] tarFile, String path) {
        return  this.dockerAPI.uploadFile(containerId, "/server/" + path, tarFile, this.dockerWebClient)
                .then(this.dockerAPI.restartContainer(containerId, this.dockerWebClient))
                .thenReturn("uploadSuesses");
    }

    private void createTarArchive(Path basePath, Path tarPath){
        try (FileOutputStream fos = new FileOutputStream(tarPath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             TarArchiveOutputStream tos = new TarArchiveOutputStream(bos)) {
            tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = basePath.relativize(file).toString();
                    TarArchiveEntry entry = new TarArchiveEntry(file.toFile(), relativePath);
                    tos.putArchiveEntry(entry);
                    Files.copy(file, tos);
                    tos.closeArchiveEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String relativePath = basePath.relativize(dir).toString();
                    if (!relativePath.isEmpty()) {
                        TarArchiveEntry entry = new TarArchiveEntry(dir.toFile(), relativePath + "/");
                        tos.putArchiveEntry(entry);
                        tos.closeArchiveEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveUserFolder_file(MultipartFile[] files, Path basePath){
            for (MultipartFile file : files) {
                String userFilePath = file.getOriginalFilename();
                String folderPathStr = "";
                int lastIndex = userFilePath.lastIndexOf('/');
                if (lastIndex != -1) {
                    folderPathStr = userFilePath.substring(0, lastIndex);
                } 
                Path folderPath = basePath.resolve(folderPathStr);
                Path filePath = basePath.resolve(userFilePath);
                try {
                    Files.createDirectories(folderPath);
                    System.out.println(folderPath);
                    Files.write(filePath, file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
