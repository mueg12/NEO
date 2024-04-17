package com.neo.back.control_edge.service;

import java.util.List;

public class EdgeServer {
    private String EdgeServerID;
    private double cpuUse;
    private double cpuIdle;
    private double memoryUse;
    private double memoryIdle;
    private double storageUse;
    private double storageIdle;
    private List<String> portUses;
    private int portSelect;

    public int getPortSelect() {
        return this.portSelect;
    }

    public void setPortSelect(int portSelect) {
        this.portSelect = portSelect;
    }

    public EdgeServer(String EdgeServerID){
        this.EdgeServerID = EdgeServerID;
    }
//-------------------------------------

    public List<String> getPortUses() {
        return this.portUses;
    }

    public void setPortUses(List<String> portUses) {
        this.portUses = portUses;
    }

    public String getEdgeServerID() {
        return this.EdgeServerID;
    }

    public void setEdgeServerID(String EdgeServerID) {
        this.EdgeServerID = EdgeServerID;
    }

    public double getCpuUse() {
        return this.cpuUse;
    }

    public void setCpuUse(double cpuUse) {
        this.cpuUse = cpuUse;
    }

    public double getCpuIdle() {
        return this.cpuIdle;
    }

    public void setCpuIdle(double cpuIdle) {
        this.cpuIdle = cpuIdle;
    }

    public double getMemoryUse() {
        return this.memoryUse;
    }

    public void setMemoryUse(double memoryUse) {
        this.memoryUse = memoryUse;
    }

    public double getMemoryIdle() {
        return this.memoryIdle;
    }

    public void setMemoryIdle(double memoryIdle) {
        this.memoryIdle = memoryIdle;
    }

    public double getStorageUse() {
        return this.storageUse;
    }

    public void setStorageUse(double storageUse) {
        this.storageUse = storageUse;
    }

    public double getStorageIdle() {
        return this.storageIdle;
    }

    public void setStorageIdle(double storageIdle) {
        this.storageIdle = storageIdle;
    }
//----------------------------------------
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
