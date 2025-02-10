package com.xm.technical.util;

import com.xm.technical.exception.CryptoNotSupportedException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {
    public static void validateCrypto(String symbol) {
        Set<String> supportedCryptos = Arrays.stream(SupportedCrypto.values())
            .map(Enum::name)
            .collect(Collectors.toSet());
        if (!supportedCryptos.contains(symbol)) {
            throw new CryptoNotSupportedException("Crypto not supported: " + symbol);
        }
    }
}
