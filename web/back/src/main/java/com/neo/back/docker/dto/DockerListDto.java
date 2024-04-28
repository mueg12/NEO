package com.neo.back.docker.dto;

import com.neo.back.springjwt.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DockerListDto {
    private User userId;
    private Long Id;
}
