package com.neo.back.docker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdgeServerCmdDTO {
    private String cpu;
    private String memoryTotal;
    private String memoryFree;
    private String storageTotal;
    private String storageAvailable;
    private String portUse;

}
