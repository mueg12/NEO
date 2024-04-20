package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDockerDto {

    private String game;

    private int ramCapacity;

    private String paymentSystem;

    private String time;

}