package com.bart.example.infrastructure.templating.usecases;

import com.bart.example.infrastructure.scheduler.annotations.Scheduled;

import javax.lang.model.element.TypeElement;

public record ScheduledMethodInfo(
        TypeElement classElement,
        String methodName,
        Scheduled scheduledAnnotation,
        boolean noArgs,
        String qualifiedMethodName
) {
    public ScheduledMethodInfo {
        if (classElement == null) {
            throw new IllegalArgumentException("classElement cannot be null");
        }
        if (methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException("methodName cannot be null or blank");
        }
        if (scheduledAnnotation == null) {
            throw new IllegalArgumentException("scheduledAnnotation cannot be null");
        }
    }

    // Convenience constructor for backward compatibility
    public ScheduledMethodInfo(TypeElement classElement, String methodName,
                               Scheduled scheduledAnnotation, boolean noArgs) {
        this(classElement, methodName, scheduledAnnotation, noArgs,
                classElement.getQualifiedName() + "." + methodName);
    }
}