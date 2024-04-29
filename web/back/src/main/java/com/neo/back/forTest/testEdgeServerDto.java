package com.neo.back.forTest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class testEdgeServerDto {
    String name;
    int totalMem;
    int useMem;

    public testEdgeServerDto(String edgeServerName, int memoryTotal, int memoryUse) {
        this.name = edgeServerName;
        this.totalMem = memoryTotal;
        this.useMem = memoryUse;
    }
}
