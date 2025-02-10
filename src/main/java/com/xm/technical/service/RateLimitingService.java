package com.xm.technical.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {
    private final LoadingCache<String, AtomicInteger> requestCounts;

    public RateLimitingService() {
        requestCounts = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public AtomicInteger load(String key) {
                    return new AtomicInteger(0);
                }
            });
    }

    public boolean tryAccess(String ipAddress, int limit) {
        AtomicInteger counter = requestCounts.getUnchecked(ipAddress);
        return counter.incrementAndGet() <= limit;
    }

    public void clearCache() {
        requestCounts.cleanUp();
    }
}
