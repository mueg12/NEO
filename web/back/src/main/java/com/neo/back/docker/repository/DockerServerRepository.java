package com.neo.back.docker.repository;

import com.neo.back.docker.entity.DockerServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerServerRepository extends JpaRepository<DockerServer, Long> {
   
    DockerServer findByUserId(Long userId);
}
