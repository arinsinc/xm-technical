package com.xm.technical.util;

import com.xm.technical.entity.CryptoPrice;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class FileReaderUtil {
    public static List<CryptoPrice> readDataFromFile(BufferedReader reader) {
        try {
            // Skip header
            reader.readLine();

            String line;
            List<CryptoPrice> priceList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                CryptoPrice price = new CryptoPrice();
                price.setDate(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Long.parseLong(values[0])),
                    ZoneId.systemDefault()));
                price.setSymbol(values[1]);
                price.setPrice(Double.parseDouble(values[2]));
                priceList.add(price);
            }
            return priceList;

        } catch (IOException e) {
            log.error("Can't parse file data");
            return Collections.emptyList();
        }
    }
}
