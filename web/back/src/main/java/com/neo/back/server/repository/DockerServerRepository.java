package com.neo.back.server.repository;

import com.neo.back.server.entity.DockerServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerServerRepository extends JpaRepository<DockerServer, Long> {
   
    DockerServer findByUserId(Long userId);
}
