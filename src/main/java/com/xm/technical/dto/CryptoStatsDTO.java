package com.xm.technical.dto;

import lombok.Data;

@Data
public class CryptoStatsDTO {
    private String symbol;
    private Double oldest;
    private Double newest;
    private Double min;
    private Double max;
    private Double normalizedRange;
}
