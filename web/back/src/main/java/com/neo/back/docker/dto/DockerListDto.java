package com.neo.back.docker.dto;

import com.neo.back.springjwt.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DockerListDto {
    private User userId;
    private Long Id;
}
