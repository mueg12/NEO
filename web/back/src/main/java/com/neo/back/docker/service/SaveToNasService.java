package com.neo.back.docker.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

// import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SaveToNasService {
    private final Path rootLocation = Paths.get("/mnt/nas");

    public Mono<ResponseEntity<String>> saveDockerImage (DataBuffer dataBuffer, String filename) throws IOException {
        Path dockerImagePath = rootLocation.resolve("dockerImage");
        // 파일 저장 경로 생성
        if (!Files.exists(dockerImagePath)) {
            Files.createDirectories(dockerImagePath);
        }
        Path path = dockerImagePath.resolve("test.tar");

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
            while (byteBuffer.hasRemaining()) {
                channel.write(byteBuffer);
            }
        } catch (IOException e) {
            DataBufferUtils.release(dataBuffer);
            return Mono.error(e);
        } finally {
            DataBufferUtils.release(dataBuffer); // 성공적으로 처리 후 자원 해제
        }
        return Mono.empty();

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
