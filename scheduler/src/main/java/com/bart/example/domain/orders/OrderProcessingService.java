package com.bart.example.domain.orders;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

@EnableScheduling
public class OrderProcessingService {

    private final List<String> processedOrders = new ArrayList<>();
    private final List<String> expiredOrders = new ArrayList<>();

    @Scheduled(fixedRate = 30000, timeUnit = TimeUnit.MILLISECONDS)
    public void processPendingOrders() {
        System.out.println("Processing pending orders at: " + java.time.LocalDateTime.now());
        // Simulate order processing logic
        processedOrders.add("ORDER-" + System.currentTimeMillis());
    }

    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void generateDailyReports() {
        System.out.println("Generating daily order reports");
        // Report generation logic
    }

    @Scheduled(fixedDelay = 60000, timeUnit = TimeUnit.MILLISECONDS)
    public void cleanupExpiredOrders() {
        System.out.println("Cleaning up expired orders");
        // Cleanup logic
        expiredOrders.add("EXPIRED-" + System.currentTimeMillis());
    }

    public int getProcessedOrdersCount() {
        return processedOrders.size();
    }

    public int getExpiredOrdersCount() {
        return expiredOrders.size();
    }
}