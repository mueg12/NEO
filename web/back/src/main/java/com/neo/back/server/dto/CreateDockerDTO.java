package com.neo.back.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDockerDTO {

    private String game;

    private String ramCapacity;

    private String paymentSystem;

    private String time;

}