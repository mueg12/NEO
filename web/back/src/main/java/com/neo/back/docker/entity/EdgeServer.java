package com.neo.back.docker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class EdgeServer {
    @Id
    private String edgeServerName;
    
    @Column(unique = true)
    private String ip;
}
