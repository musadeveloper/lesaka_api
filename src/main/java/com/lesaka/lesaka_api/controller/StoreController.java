package com.lesaka.lesaka_api.controller;

import com.lesaka.lesaka_api.dto.CashUpResponse;
import com.lesaka.lesaka_api.dto.StoreRequest;
import com.lesaka.lesaka_api.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Tag(name = "Store API", description = "API for store cash up operations")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/cashup")
    @Operation(summary = "Cash up the store with amount", description = "Processes cash up and sends amount to bank API")
    public ResponseEntity<String> cashUp(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cash up request", required = true) @RequestBody StoreRequest request) {
        CashUpResponse response = storeService.cashUp(request);
        if (response != null) {
            String fullMessage = "Customer : " + response.getCustomer() + "\n" + response.getMessage();
            return ResponseEntity.ok(fullMessage);
        } else {
            return ResponseEntity.badRequest().body("Cash up failed. Please check the amount or try again later.");
        }
    }
}
