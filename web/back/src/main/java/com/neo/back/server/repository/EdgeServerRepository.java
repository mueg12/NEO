package com.neo.back.server.repository;

import com.neo.back.server.entity.EdgeServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeServerRepository extends JpaRepository<EdgeServer, Long> {
    EdgeServer findByEdgeServerName(String EdgeServerName);
}
