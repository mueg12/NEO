package com.neo.back.docker.repository;

import com.neo.back.docker.entity.EdgeServerEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeServerRepository extends JpaRepository<EdgeServerEntity, String> {
    EdgeServerEntity findByEdgeServerName(String EdgeServerName);
    EdgeServerEntity save(EdgeServerEntity edgeServer);
    List<EdgeServerEntity> findAll();
    
}
