package com.neo.back.docker.repository;

import com.neo.back.docker.entity.DockerServer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerServerRepository extends JpaRepository<DockerServer, Long> {
   
    Optional<DockerServer> findById(Long id);
    List<DockerServer> findAllByUserId(Long id);
}
