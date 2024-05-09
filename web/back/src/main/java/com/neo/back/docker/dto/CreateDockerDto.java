package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateDockerDto {

    private String gameName;

    private String version;

    private Long imageNum;

    private String serverName;

    private int ramCapacity;

    private String paymentSystem;

    private int time;

    private String baseImage;

}