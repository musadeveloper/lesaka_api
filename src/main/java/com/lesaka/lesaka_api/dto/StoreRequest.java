package com.lesaka.lesaka_api.dto;

import lombok.Data;

@Data
public class StoreRequest {
    private String name;
    private double amount;

    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }
}
