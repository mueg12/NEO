package com.neo.back.docker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="edgeServer")
public class EdgeServerEntity {
    @Id
    private String edgeServerName;

    private String ip;
    private String user;
    private String passWord;
}
