package com.bart.example.application.services;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
public class TestScheduledService {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicInteger fixedRateCounter = new AtomicInteger(0);
    private final AtomicInteger fixedDelayCounter = new AtomicInteger(0);

    @Scheduled(fixedRate = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void executeEverySecond() {
        int count = counter.incrementAndGet();
        System.out.println("Fixed rate task executed " + count + " times");
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void executeWithDelay() {
        int count = fixedDelayCounter.incrementAndGet();
        System.out.println("Fixed delay task executed " + count + " times");
    }

    @Scheduled(cron = "0/5 * * * * *") // Every 5 seconds
    public void executeCronJob() {
        int count = fixedRateCounter.incrementAndGet();
        System.out.println("Cron task executed " + count + " times");
    }

    @Scheduled(fixedRate = 5000, timeUnit = TimeUnit.MILLISECONDS)
    public void healthCheck() {
        System.out.println("Health check: All scheduled tasks are running");
    }

    // Getter methods for testing
    public int getCounter() {
        return counter.get();
    }

    public int getFixedRateCounter() {
        return fixedRateCounter.get();
    }

    public int getFixedDelayCounter() {
        return fixedDelayCounter.get();
    }
}