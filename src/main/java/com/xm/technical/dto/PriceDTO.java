package com.xm.technical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PriceDTO {
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("date")
    private LocalDateTime date;
    @JsonProperty("price")
    private Double price;
}
