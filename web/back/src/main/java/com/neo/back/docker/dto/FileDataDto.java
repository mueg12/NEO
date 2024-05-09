package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDataDto {
    private String fileName;
    private Boolean isDirectory;
}
