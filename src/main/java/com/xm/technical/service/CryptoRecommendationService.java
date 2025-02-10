package com.xm.technical.service;

import com.xm.technical.dao.CryptoPriceRepository;
import com.xm.technical.dto.CryptoStatsDTO;
import com.xm.technical.dto.FileUploadResponseDTO;
import com.xm.technical.dto.PriceDTO;
import com.xm.technical.dto.PriceRequestDTO;
import com.xm.technical.entity.CryptoPrice;
import com.xm.technical.exception.PriceNotFoundException;
import com.xm.technical.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoRecommendationService {
    private final CryptoPriceRepository priceRepository;
    private final PriceUploadService priceUploadService;

    /**
     * Get all crypto stats
     * @return List<CryptoStatsDTO>
     */
    public List<CryptoStatsDTO> getAllCryptoStats() {
        List<String> symbols = priceRepository.findDistinctSymbols();
        return symbols.stream()
            .map(s -> calculateCryptoStats(s, 1))
            .sorted(Comparator.comparing(CryptoStatsDTO::getNormalizedRange).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Get cryto stat by symbol
     * @param symbol - Crypto symbol
     * @return CryptoStatsDTO
     */
    public CryptoStatsDTO getCryptoStats(String symbol) {
        ValidationUtil.validateCrypto(symbol);
        return calculateCryptoStats(symbol, 1);
    }

    /**
     * Get highest normalized range
     * @param date - Date
     * @return String
     */
    public String getHighestNormalizedRangeForDate(LocalDate date) {
        List<CryptoPrice> prices = priceRepository.findByDateBetween(date.atStartOfDay(), date.atStartOfDay().plusHours(24));
        if (prices.isEmpty()) {
            throw new PriceNotFoundException("Price not found for the date: " + date);
        }
        Map<String, List<CryptoPrice>> pricesBySymbol = prices.stream()
            .collect(Collectors.groupingBy(CryptoPrice::getSymbol));

        String highestSymbol = "";
        double highestRange = 0.0;

        for (Map.Entry<String, List<CryptoPrice>> entry : pricesBySymbol.entrySet()) {
            List<CryptoPrice> symbolPrices = entry.getValue();
            double min = symbolPrices.stream().mapToDouble(CryptoPrice::getPrice).min().orElse(0.0);
            double max = symbolPrices.stream().mapToDouble(CryptoPrice::getPrice).max().orElse(0.0);
            double normalizedRange = min != 0 ? (max - min) / min : 0;

            if (normalizedRange > highestRange) {
                highestRange = normalizedRange;
                highestSymbol = entry.getKey();
            }
        }

        return highestSymbol;
    }

    private CryptoStatsDTO calculateCryptoStats(String symbol, int range) {
        List<CryptoPrice> prices = priceRepository.findBySymbol(symbol);

        if (prices.isEmpty()) {
            throw new PriceNotFoundException("Price not found for symbol: " + symbol);
        }

        CryptoStatsDTO stats = new CryptoStatsDTO();
        stats.setSymbol(symbol);

        // Sort by timestamp
        prices.sort(Comparator.comparing(CryptoPrice::getDate));
        LocalDateTime endDate = prices.get(0).getDate().plusMonths(range);

        stats.setOldest(prices.get(0).getPrice());
        List<CryptoPrice> filteredList = prices.stream().filter(p -> p.getDate().isBefore(endDate)).toList();
        stats.setNewest(
            filteredList.get(filteredList.size()-1).getPrice()
        );

        double min = prices.stream().mapToDouble(CryptoPrice::getPrice).min().getAsDouble();
        double max = prices.stream().mapToDouble(CryptoPrice::getPrice).max().getAsDouble();

        stats.setMin(min);
        stats.setMax(max);
        stats.setNormalizedRange((max - min) / min);

        return stats;
    }

    /**
     * Get all prices
     * @return List<PriceDTO>
     */
    public List<PriceDTO> getAllPrices() {
        List<CryptoPrice> prices = priceRepository.findAll();
        List<PriceDTO> priceDTOS = new ArrayList<>();
        prices.forEach(p -> priceDTOS.add(
            PriceDTO.builder()
                .symbol(p.getSymbol())
                .date(p.getDate())
                .price(p.getPrice())
                .build()
        ));
        return priceDTOS;
    }

    /**
     * Save prices from csv file
     * @param file - File
     * @return FileUploadResponseDTO
     */
    public FileUploadResponseDTO savePrices(MultipartFile file) {
        return priceUploadService.processCsvFile(file);
    }

    /**
     * Save price
     * @param priceDTO - PriceRequestDTO
     */
    public void savePrice(PriceRequestDTO priceDTO) {
        ValidationUtil.validateCrypto(priceDTO.getSymbol());
        CryptoPrice price = CryptoPrice.builder()
            .symbol(priceDTO.getSymbol())
            .date(
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(priceDTO.getDate()),
                    ZoneId.systemDefault())
            )
            .price(priceDTO.getPrice())
            .build();
        priceRepository.save(price);
    }


}
