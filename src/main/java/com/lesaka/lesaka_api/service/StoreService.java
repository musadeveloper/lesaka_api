package com.lesaka.lesaka_api.service;

import com.lesaka.lesaka_api.dto.CashUpResponse;
import com.lesaka.lesaka_api.dto.StoreRequest;
import com.lesaka.lesaka_api.entity.Profit;
import com.lesaka.lesaka_api.entity.Store;
import com.lesaka.lesaka_api.repository.ProfitRepository;
import com.lesaka.lesaka_api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final ProfitRepository profitRepository;
    private final WebClient webClient = WebClient.create("https://testbank.postfunds.co.za");


    public CashUpResponse cashUp(StoreRequest request) {
        try {
            if (request.getAmount() < 0) {
                log.error("Invalid amount: {}", request.getAmount());
                return null;
            }

            double transactionFee = (request.getAmount() / 100) * 0.11;
            double customerAmount = request.getAmount() - transactionFee;

            Store safeAmountObj = Store.builder()
                    .name(request.getName())
                    .amount(request.getAmount())
                    .build();

            Store store = Store.builder()
                    .name(request.getName())
                    .amount(customerAmount)
                    .build();

            Profit profit = Profit.builder()
                    .fee(transactionFee)
                    .build();

            webClient.post()
                    .bodyValue(safeAmountObj)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            storeRepository.save(store);
            profitRepository.save(profit);

            return new CashUpResponse(request.getName(),
                    "Current amount in smart safe : R" + request.getAmount());
        } catch (Exception e) {
            log.error("Cash up failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
