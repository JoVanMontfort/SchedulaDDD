package com.bart.example.application;

import com.bart.example.application.notifications.EmailNotificationService;
import com.bart.example.application.services.TestScheduledService;
import com.bart.example.application.test.ComprehensiveSchedulerTest;
import com.bart.example.domain.orders.OrderProcessingService;
import com.bart.example.infrastructure.cache.CacheService;

public class SchedulerTestRunner {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Scheduler Tests...");

        // Initialize services (schedulers will be auto-generated during compilation)
        TestScheduledService testService = new TestScheduledService();
        OrderProcessingService orderService = new OrderProcessingService();
        CacheService cacheService = new CacheService();
        EmailNotificationService emailService = new EmailNotificationService();
        ComprehensiveSchedulerTest comprehensiveTest = new ComprehensiveSchedulerTest();

        // Simulate some work
        cacheService.put("key1", "value1", 30000);
        cacheService.put("key2", "value2", 10000);

        emailService.queueEmail(new EmailNotificationService.EmailMessage(
                "user@example.com", "Test Email", "This is a test email"));

        // Let the schedulers run for a while
        System.out.println("Letting schedulers run for 30 seconds...");
        Thread.sleep(30000);

        // Print results
        System.out.println("\n=== Test Results ===");
        System.out.println("Test Service executions: " + testService.getCounter());
        System.out.println("Orders processed: " + orderService.getProcessedOrdersCount());
        System.out.println("Cache size: " + cacheService.size());
        System.out.println("Emails sent: " + emailService.getSentEmailsCount());
        System.out.println("Comprehensive test counts: " +
                comprehensiveTest.getFastTaskCount() + "/" +
                comprehensiveTest.getSlowTaskCount() + "/" +
                comprehensiveTest.getCronTaskCount() + "/" +
                comprehensiveTest.getFixedDelayTaskCount());

        System.out.println("Scheduler tests completed!");
    }
}