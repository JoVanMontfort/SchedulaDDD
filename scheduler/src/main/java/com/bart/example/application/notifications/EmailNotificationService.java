package com.bart.example.application.notifications;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@EnableScheduling
public class EmailNotificationService {

    private final BlockingQueue<EmailMessage> emailQueue = new LinkedBlockingQueue<>();
    private int sentEmails = 0;

    @Scheduled(fixedRate = 5000, timeUnit = TimeUnit.MILLISECONDS)
    public void processEmailQueue() {
        int processed = 0;
        while (!emailQueue.isEmpty()) {
            EmailMessage message = emailQueue.poll();
            if (message != null) {
                sendEmail(message);
                processed++;
            }
        }
        if (processed > 0) {
            System.out.println("Processed " + processed + " emails from queue");
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Daily at 9 AM
    public void sendDailyDigest() {
        EmailMessage digest = new EmailMessage("daily-digest@example.com",
                "Daily Digest", "Your daily summary...");
        sendEmail(digest);
    }

    @Scheduled(fixedDelay = 30000, timeUnit = TimeUnit.MILLISECONDS)
    public void cleanupOldNotifications() {
        System.out.println("Cleaning up old notification records");
        // Cleanup logic
    }

    public void queueEmail(EmailMessage message) {
        emailQueue.offer(message);
    }

    private void sendEmail(EmailMessage message) {
        // Simulate email sending
        System.out.println("Sending email to: " + message.recipient() +
                " - Subject: " + message.subject());
        sentEmails++;
    }

    public int getSentEmailsCount() {
        return sentEmails;
    }

    public int getQueueSize() {
        return emailQueue.size();
    }

    public record EmailMessage(String recipient, String subject, String body) {
    }
}