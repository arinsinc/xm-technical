package com.xm.technical.config;

import com.xm.technical.dao.CryptoPriceRepository;
import com.xm.technical.util.FileReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CryptoDataLoader {
    private final CryptoPriceRepository priceRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            String[] cryptoFiles = {"BTC_values.csv", "DOGE_values.csv", "ETH_values.csv", "LTC_values.csv", "XRP_values.csv"};

            for (String file : cryptoFiles) {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream("/data/" + file)))) {
                    priceRepository.saveAll(Objects.requireNonNull(FileReaderUtil.readDataFromFile(reader)));
                } catch (Exception ex) {
                    log.error("Can't save data from file");
                }
            }
        };
    }
}
