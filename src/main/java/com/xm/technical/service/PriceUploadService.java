package com.xm.technical.service;

import com.xm.technical.dao.CryptoPriceRepository;
import com.xm.technical.dto.FileUploadResponseDTO;
import com.xm.technical.entity.CryptoPrice;
import com.xm.technical.exception.FileUploadException;
import com.xm.technical.util.FileReaderUtil;
import com.xm.technical.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PriceUploadService {
    private final CryptoPriceRepository priceRepository;

    public FileUploadResponseDTO processCsvFile(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new FileUploadException("File is empty");
            }

            if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".csv")) {
                throw new FileUploadException("File must be a CSV file");
            }

            // Process CSV file
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                List<CryptoPrice> priceList = FileReaderUtil.readDataFromFile(reader);
                ValidationUtil.validateCrypto(priceList.get(0).getSymbol());
                priceRepository.saveAll(Objects.requireNonNull(priceList));

                return FileUploadResponseDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .recordsProcessed(priceList.size())
                    .status("success")
                    .message("File processed successfully")
                    .build();

            }
        } catch (Exception e) {
            return FileUploadResponseDTO.builder()
                .fileName(file.getOriginalFilename())
                .status("error")
                .message("Error processing file: " + e.getMessage())
                .build();
        }
    }
}
