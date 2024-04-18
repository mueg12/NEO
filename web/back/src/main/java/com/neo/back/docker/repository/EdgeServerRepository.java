package com.neo.back.docker.repository;

import com.neo.back.docker.entity.EdgeServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeServerRepository extends JpaRepository<EdgeServer, Long> {
    EdgeServer findByEdgeServerName(String EdgeServerName);
}
