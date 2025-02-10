package com.xm.technical.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseSerializer {
    String code;
    @Builder.Default
    boolean success = true;
    Object data;
    String message;
}
