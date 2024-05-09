package com.neo.back.docker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class EdgeServer {
    @Id
    private String edgeServerName;
    
    @Column(unique = true)
    private String ip;
    private String domainName;
    private String user;
    private String passWord;
    private int memoryTotal;
    private int memoryUse;
}
