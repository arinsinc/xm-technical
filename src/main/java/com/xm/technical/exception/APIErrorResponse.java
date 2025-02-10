package com.xm.technical.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class APIErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timeStamp;

    public APIErrorResponse() {}

    public APIErrorResponse(String code, String message, LocalDateTime timeStamp) {
        this.code = code;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
