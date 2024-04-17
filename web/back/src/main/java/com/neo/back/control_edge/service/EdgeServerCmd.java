package com.neo.back.control_edge.service;

class EdgeServerCmd {
    private String cpu;
    private String memoryTotal;
    private String memoryFree;
    private String storageTotal;
    private String storageAvailable;
    private String portUse;

    public Object getPortUse() {
        return this.portUse;
    }

    public void setPortUse(String portUse) {
        this.portUse = portUse;
    }

    public String getCpu() {
        return this.cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemoryTotal() {
        return this.memoryTotal;
    }

    public void setMemoryTotal(String memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public String getMemoryFree() {
        return this.memoryFree;
    }

    public void setMemoryFree(String memoryFree) {
        this.memoryFree = memoryFree;
    }

    public String getStorageTotal() {
        return this.storageTotal;
    }

    public void setStorageTotal(String storageTotal) {
        this.storageTotal = storageTotal;
    }

    public String getStorageAvailable() {
        return this.storageAvailable;
    }

    public void setStorageAvailable(String storageAvailable) {
        this.storageAvailable = storageAvailable;
    }

}
