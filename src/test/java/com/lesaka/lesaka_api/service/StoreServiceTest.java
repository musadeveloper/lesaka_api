package com.lesaka.lesaka_api.service;

import com.lesaka.lesaka_api.dto.StoreRequest;
import com.lesaka.lesaka_api.entity.Profit;
import com.lesaka.lesaka_api.entity.Store;
import com.lesaka.lesaka_api.repository.ProfitRepository;
import com.lesaka.lesaka_api.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProfitRepository profitRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    public void setup() throws Exception {
        // No need to call MockitoAnnotations.openMocks(this) with @ExtendWith(MockitoExtension.class)

        Field webClientField = StoreService.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(storeService, webClient);

        // Mock the WebClient call chain
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testCashUp_Success() {
        StoreRequest request = new StoreRequest();
        request.setName("Astron Energy");
        request.setAmount(135000.0);

        storeService.cashUp(request);

        double expectedFee = (135000 / 100) * 0.11;
        double expectedCustomerAmount = 135000 - expectedFee;

        verify(webClient, times(1)).post();

        ArgumentCaptor<Store> storeCaptor = ArgumentCaptor.forClass(Store.class);
        verify(storeRepository, times(1)).save(storeCaptor.capture());
        Store savedStore = storeCaptor.getValue();
        assert savedStore.getName().equals("Astron Energy");
        assert Math.abs(savedStore.getAmount() - expectedCustomerAmount) < 0.0001;

        ArgumentCaptor<Profit> profitCaptor = ArgumentCaptor.forClass(Profit.class);
        verify(profitRepository, times(1)).save(profitCaptor.capture());
        Profit savedProfit = profitCaptor.getValue();
        assert Math.abs(savedProfit.getFee() - expectedFee) < 0.0001;
    }

    @Test
    void testCashUp_WebClientErrorHandled() {
        StoreRequest request = new StoreRequest();
        request.setName("Astron Energy");
        request.setAmount(135000.0);

        // Mock WebClient chain to simulate error
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException("API failure")));

        storeService.cashUp(request);

        // Verify repositories still saved even if WebClient fails
        verify(storeRepository, times(1)).save(any(Store.class));
        verify(profitRepository, times(1)).save(any(Profit.class));

        // Optionally verify webClient.post was called
        verify(webClient, times(1)).post();
    }
}
