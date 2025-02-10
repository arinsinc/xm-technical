package com.xm.technical.exception;

public class CryptoNotSupportedException extends RuntimeException {
    public CryptoNotSupportedException(String message) {
        super(message);
    }
}
