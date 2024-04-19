package com.neo.back.docker.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdgeServerInfoDTO {
    private String EdgeServerID;
    private double cpuUse;
    private double cpuIdle;
    private double memoryUse;
    private double memoryIdle;
    private double storageUse;
    private double storageIdle;
    private List<String> portUses;
    private int portSelect;

    public EdgeServerInfoDTO(String EdgeServerID){
        this.EdgeServerID = EdgeServerID;
    }

    public double getMemoryUsePercent() {
        return this.memoryUse * 100 /(this.memoryUse + this.memoryIdle);
    }

    public double getMemoryIdlePercent() {
        return this.memoryIdle * 100 /(this.memoryUse + this.memoryIdle);
    }
    
    public double getStorageUsePercent() {
        return this.storageUse * 100 /(this.storageUse + this.storageIdle);
    }

    public double getStorageIdlePercent() {
        return this.storageIdle * 100 /(this.storageUse + this.storageIdle);
    }
}
