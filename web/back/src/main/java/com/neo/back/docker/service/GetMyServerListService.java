package com.neo.back.docker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.neo.back.docker.dto.MyServerListDto;
import com.neo.back.docker.entity.DockerImage;
import com.neo.back.docker.repository.DockerImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GetMyServerListService {
    private final DockerImageRepository dockerImageRepo;

    public List<MyServerListDto> getMyServerList() {
        List<DockerImage> dockerImages = dockerImageRepo.findByUser(null);

        return dockerImages.stream()
            .map(image -> new MyServerListDto(image.getId(), image.getGame().getGameName(),image.getGame().getVersion(), image.getServerName(), image.getDate()))
            .collect(Collectors.toList());
    }
}
