package com.xm.technical.service;

import com.xm.technical.dao.CryptoPriceRepository;
import com.xm.technical.dto.FileUploadResponseDTO;
import com.xm.technical.entity.CryptoPrice;
import com.xm.technical.util.FileReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @Mock
    private CryptoPriceRepository priceRepository;

    @InjectMocks
    private PriceUploadService priceUploadService;

    @Test
    void testProcessCsvFile_EmptyFile_ShouldThrowException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        FileUploadResponseDTO response = priceUploadService.processCsvFile(file);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertTrue(response.getMessage().contains("File is empty"));
    }

    @Test
    void testProcessCsvFile_InvalidFileFormat_ShouldThrowException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("invalid.txt");

        FileUploadResponseDTO response = priceUploadService.processCsvFile(file);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertTrue(response.getMessage().contains("File must be a CSV file"));
    }

    @Test
    void testProcessCsvFile_SuccessfulProcessing() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("prices.csv");

        String csvContent = "BTC,2024-02-08T10:00:00,50000.0\nETH,2024-02-08T10:00:00,3000.0";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        List<CryptoPrice> mockPrices = List.of(
            new CryptoPrice(1L, "BTC", LocalDateTime.now(), 50000.0, LocalDateTime.now(), LocalDateTime.now()),
            new CryptoPrice(2L, "ETH", LocalDateTime.now(), 3000.0, LocalDateTime.now(), LocalDateTime.now())
        );

        mockStatic(FileReaderUtil.class);
        when(FileReaderUtil.readDataFromFile(any(BufferedReader.class))).thenReturn(mockPrices);

        FileUploadResponseDTO response = priceUploadService.processCsvFile(file);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals(2, response.getRecordsProcessed());
        assertEquals("prices.csv", response.getFileName());
        verify(priceRepository, times(1)).saveAll(mockPrices);
    }

    @Test
    void testProcessCsvFile_ExceptionHandling() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("prices.csv");
        when(file.getInputStream()).thenThrow(new IOException("File read error"));

        FileUploadResponseDTO response = priceUploadService.processCsvFile(file);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertTrue(response.getMessage().contains("Error processing file"));
    }
}
