package com.xm.technical.dao;

import com.xm.technical.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    List<CryptoPrice> findBySymbol(String symbol);

    List<CryptoPrice> findBySymbolAndDateBetween(String symbol, LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT c.symbol FROM CryptoPrice c")
    List<String> findDistinctSymbols();

    List<CryptoPrice> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
