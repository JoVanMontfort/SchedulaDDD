package com.bart.example.infrastructure.cache;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EnableScheduling
public class CacheService {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private int evictionCount = 0;

    @Scheduled(fixedRate = 60000, timeUnit = TimeUnit.MILLISECONDS) // Every minute
    public void evictExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        int evicted = 0;

        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired(currentTime)) {
                cache.remove(entry.getKey());
                evicted++;
            }
        }

        evictionCount += evicted;
        System.out.println("Evicted " + evicted + " expired cache entries. Total: " + evictionCount);
    }

    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void logCacheStatistics() {
        System.out.println("Cache statistics - Size: " + cache.size() + ", Total evictions: " + evictionCount);
    }

    public void put(String key, Object value, long ttlMillis) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMillis));
    }

    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        return (entry != null && !entry.isExpired(System.currentTimeMillis())) ? entry.value : null;
    }

    public int size() {
        return cache.size();
    }

    private static class CacheEntry {
        final Object value;
        final long expirationTime;

        CacheEntry(Object value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        boolean isExpired(long currentTime) {
            return currentTime >= expirationTime;
        }
    }
}