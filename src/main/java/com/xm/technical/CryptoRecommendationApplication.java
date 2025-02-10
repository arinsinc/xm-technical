package com.xm.technical;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Crypto Recommendation API",
        version = "1.0",
        description = "API for crypto price analysis and recommendations"
    )
)
public class CryptoRecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoRecommendationApplication.class, args);
    }
}
