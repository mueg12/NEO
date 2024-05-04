package com.neo.back.docker.dto;

import com.neo.back.springjwt.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DockerListDto {
    private UserEntity userId;
    private Long Id;
}
