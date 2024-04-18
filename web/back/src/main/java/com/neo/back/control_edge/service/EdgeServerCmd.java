package com.neo.back.control_edge.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class EdgeServerCmd {
    private String cpu;
    private String memoryTotal;
    private String memoryFree;
    private String storageTotal;
    private String storageAvailable;
    private String portUse;

}
