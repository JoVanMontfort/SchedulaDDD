package com.bart.example.application.test;


import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@EnableScheduling
public class ComprehensiveSchedulerTest {

    private final AtomicLong task1Count = new AtomicLong(0);
    private final AtomicLong task2Count = new AtomicLong(0);
    private final AtomicLong task3Count = new AtomicLong(0);
    private final AtomicLong task4Count = new AtomicLong(0);

    // Fast task - runs every second
    @Scheduled(fixedRate = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void fastTask() {
        task1Count.incrementAndGet();
        System.out.println("Fast task executed: " + task1Count.get());
    }

    // Slow task - runs every 5 seconds with initial delay
    @Scheduled(fixedRate = 5000, initialDelay = 2000, timeUnit = TimeUnit.MILLISECONDS)
    public void slowTask() {
        task2Count.incrementAndGet();
        System.out.println("Slow task executed: " + task2Count.get());
    }

    // Cron-based task - runs every 10 seconds
    @Scheduled(cron = "0/10 * * * * *")
    public void cronTask() {
        task3Count.incrementAndGet();
        System.out.println("Cron task executed: " + task3Count.get());
    }

    // Fixed delay task - runs with 3 second delay between completions
    @Scheduled(fixedDelay = 3000, timeUnit = TimeUnit.MILLISECONDS)
    public void fixedDelayTask() {
        task4Count.incrementAndGet();
        System.out.println("Fixed delay task executed: " + task4Count.get());
        try {
            Thread.sleep(500); // Simulate some work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Getter methods for testing
    public long getFastTaskCount() {
        return task1Count.get();
    }

    public long getSlowTaskCount() {
        return task2Count.get();
    }

    public long getCronTaskCount() {
        return task3Count.get();
    }

    public long getFixedDelayTaskCount() {
        return task4Count.get();
    }
}