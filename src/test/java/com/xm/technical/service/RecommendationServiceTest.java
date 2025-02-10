package com.xm.technical.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.xm.technical.dao.CryptoPriceRepository;
import com.xm.technical.dto.CryptoStatsDTO;
import com.xm.technical.dto.FileUploadResponseDTO;
import com.xm.technical.dto.PriceDTO;
import com.xm.technical.dto.PriceRequestDTO;
import com.xm.technical.entity.CryptoPrice;
import com.xm.technical.exception.PriceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private CryptoPriceRepository priceRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PriceUploadService priceUploadService;

    @InjectMocks
    private CryptoRecommendationService cryptoRecommendationService;

    private List<CryptoPrice> samplePrices;

    @BeforeEach
    void setUp() {
        samplePrices = Arrays.asList(
            new CryptoPrice(1L, "BTC", LocalDateTime.now(), 40000.0, LocalDateTime.now(), LocalDateTime.now()),
            new CryptoPrice(2L, "BTC", LocalDateTime.now().minusDays(1), 38000.0, LocalDateTime.now(), LocalDateTime.now()),
            new CryptoPrice(3L, "BTC", LocalDateTime.now().minusDays(2), 35000.0,  LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @Test
    void testGetAllCryptoStats() {
        when(priceRepository.findDistinctSymbols()).thenReturn(Arrays.asList("BTC", "ETH"));
        when(priceRepository.findBySymbol(anyString())).thenReturn(samplePrices);

        List<CryptoStatsDTO> stats = cryptoRecommendationService.getAllCryptoStats();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    @Test
    void testGetCryptoStats() {
        when(priceRepository.findBySymbol("BTC")).thenReturn(samplePrices);

        CryptoStatsDTO stats = cryptoRecommendationService.getCryptoStats("BTC");
        assertNotNull(stats);
        assertEquals("BTC", stats.getSymbol());
    }

    @Test
    void testGetHighestNormalizedRangeForDate() {
        when(priceRepository.findByDateBetween(any(), any())).thenReturn(samplePrices);

        String highestSymbol = cryptoRecommendationService.getHighestNormalizedRangeForDate(LocalDate.now());
        assertEquals("BTC", highestSymbol);
    }

    @Test
    void testGetHighestNormalizedRangeForDate_NoPrices() {
        when(priceRepository.findByDateBetween(any(), any())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(PriceNotFoundException.class, () ->
            cryptoRecommendationService.getHighestNormalizedRangeForDate(LocalDate.now()));
        assertTrue(exception.getMessage().contains("Price not found"));
    }

    @Test
    void testGetAllPrices() {
        when(priceRepository.findAll()).thenReturn(samplePrices);

        List<PriceDTO> prices = cryptoRecommendationService.getAllPrices();
        assertNotNull(prices);
        assertEquals(3, prices.size());
    }

    @Test
    void testSavePrices() {
        MultipartFile file = mock(MultipartFile.class);
        FileUploadResponseDTO responseDTO = new FileUploadResponseDTO("btc.csv", 3, "success", "Done");
        when(priceUploadService.processCsvFile(file)).thenReturn(responseDTO);

        FileUploadResponseDTO response = cryptoRecommendationService.savePrices(file);
        assertNotNull(response);
        assertEquals("success", response.getStatus());
    }

    @Test
    void testSavePrice() {
        PriceRequestDTO priceDTO = new PriceRequestDTO("BTC", System.currentTimeMillis(), 42000.0);

        cryptoRecommendationService.savePrice(priceDTO);
        verify(priceRepository, times(1)).save(any(CryptoPrice.class));
    }
}

