package com.bart.example.infrastructure.scheduler.usecases;

import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import javax.lang.model.element.TypeElement;

public record ScheduledMethodInfo(
        TypeElement classElement,
        String methodName,
        Scheduled scheduledAnnotation,
        boolean noArgs
) {
}