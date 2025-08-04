package com.lesaka.lesaka_api.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CashUpResponse {
    private String customer;
    private String message;

    public CashUpResponse(String customer, String message) {
        this.customer = customer;
        this.message = message;
    }
}
