package com.example.test_pro.model.config;

public class StorageModel {
    private final String total;
    private final String used;
    private final String free;

    public StorageModel(String total, String used, String free) {
        this.total = total;
        this.used = used;
        this.free = free;
    }

    public String getTotal() {
        return total;
    }

    public String getUsed() {
        return used;
    }

    public String getFree() {
        return free;
    }
}
