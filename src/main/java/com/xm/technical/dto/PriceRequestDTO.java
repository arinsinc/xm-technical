package com.xm.technical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PriceRequestDTO {
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("date")
    private Long date;
    @JsonProperty("price")
    private Double price;
}
