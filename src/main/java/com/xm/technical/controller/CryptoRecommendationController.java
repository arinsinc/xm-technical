package com.xm.technical.controller;

import com.xm.technical.annotation.RateLimit;
import com.xm.technical.dto.PriceRequestDTO;
import com.xm.technical.service.CryptoRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/crypto")
@RequiredArgsConstructor
@Tag(name = "Crypto Recommendation API", description = "API endpoints for crypto recommendations")
public class CryptoRecommendationController {
    private final CryptoRecommendationService cryptoService;

    @GetMapping("/stats")
    @Operation(summary = "Get all crypto statistics sorted by normalized range")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> getAllCryptoStats() {
        ResponseSerializer response = ResponseSerializer.builder()
            .code("STAT")
            .data(cryptoService.getAllCryptoStats())
            .message("Crypto stats fetched successfully")
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/{symbol}")
    @Operation(summary = "Get statistics for a specific crypto")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> getCryptoStats(@PathVariable String symbol) {
        ResponseSerializer response = ResponseSerializer.builder()
            .code("SYMBOL")
            .data(cryptoService.getCryptoStats(symbol))
            .message("Crypto stats fetched successfully for symbol")
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/highest-normalized-range")
    @Operation(summary = "Get crypto with highest normalized range for a specific date")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> getHighestNormalizedRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ResponseSerializer response = ResponseSerializer.builder()
            .code("RANGE")
            .data(cryptoService.getHighestNormalizedRangeForDate(date))
            .message("Crypto with highest normalized range fetched successfully")
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prices")
    @Operation(summary = "Get all prices")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> getAllPrices() {
        ResponseSerializer response = ResponseSerializer.builder()
            .code("PRICE")
            .data(cryptoService.getAllPrices())
            .message("All prices fetched successfully")
            .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/prices")
    @Operation(summary = "Save prices for Crypto")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> savePrices(@RequestParam MultipartFile file) {
        ResponseSerializer response = ResponseSerializer.builder()
            .code("PRICE")
            .data(cryptoService.savePrices(file))
            .message("All prices saved successfully")
            .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/price")
    @Operation(summary = "Save price for Crypto")
    @RateLimit(requests = 10, duration = 60)
    public ResponseEntity<ResponseSerializer> savePrice(@RequestBody PriceRequestDTO priceDTO) {
        cryptoService.savePrice(priceDTO);
        ResponseSerializer response = ResponseSerializer.builder()
            .code("PRICE")
            .data(null)
            .message("Price saved successfully")
            .build();
        return ResponseEntity.ok(response);
    }
}
