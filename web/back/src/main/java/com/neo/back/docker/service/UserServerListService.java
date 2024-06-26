package com.neo.back.docker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.file.*;

import com.neo.back.springjwt.entity.User;
import org.springframework.stereotype.Service;

import com.neo.back.docker.dto.MyServerListDto;
import com.neo.back.docker.entity.DockerImage;
import com.neo.back.docker.repository.DockerImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServerListService {
    private final DockerImageRepository dockerImageRepo;

    public List<MyServerListDto> getServerList(User user) {
        List<DockerImage> dockerImages = dockerImageRepo.findByUser(user);

        return dockerImages.stream()
            .map(image -> new MyServerListDto(image.getId(), image.getGame().getGameName(),image.getGame().getVersion(), image.getServerName(), image.getDate()))
            .collect(Collectors.toList());
    }

    public Mono<String> deleteServer(Long ImageNum) {
        Path dockerImagePath = Paths.get("/mnt/nas/dockerImage");
        Optional<DockerImage> dockerImage = dockerImageRepo.findById(ImageNum);
        Path path = dockerImagePath.resolve(dockerImage.get().getServerName() + "_" + dockerImage.get().getUser().getId() + ".tar");
        try {
            Files.delete(path);
            dockerImageRepo.deleteById(ImageNum);
            return Mono.just("Delete image success");
        } catch (Exception e) {
            return Mono.error(new NoSuchFileException("Delete image fail"));
        }
    }

    // public Mono<String> renameServer() {
        
    // }
}
