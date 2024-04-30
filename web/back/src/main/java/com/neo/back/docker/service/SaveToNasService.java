package com.neo.back.docker.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

// import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SaveToNasService {
    private final Path rootLocation = Paths.get("/mnt/nas");

    public Mono<ResponseEntity<String>> saveDockerImage (byte[] file) {
        Path path = rootLocation.resolve("dockerImage");
        try {
            // 파일 저장 경로 생성
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            // 파일 저장
            // Path destinationFile = path.resolve(
            //     Paths.get(file.getOriginalFilename()))
            //     .normalize().toAbsolutePath();
            Files.write(path, file);

            return Mono.just(ResponseEntity.ok("File uploaded successfully"));
        } catch (IOException e) {
            return Mono.just(ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage()));
        }
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> handleStorageFileNotFound(StorageException e) {
        return ResponseEntity.notFound().build();
    }

    private static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }
    }

}
