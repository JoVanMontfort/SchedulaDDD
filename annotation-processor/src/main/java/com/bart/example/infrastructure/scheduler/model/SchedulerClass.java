package com.bart.example.infrastructure.scheduler.model;

import com.bart.example.infrastructure.templating.usecases.ScheduledMethodInfo;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.List;

public record SchedulerClass(
        TypeElement classElement,
        List<ScheduledMethodInfo> scheduledMethods,
        Filer filer,
        String packageName,
        String className
) {
    public static SchedulerClassBuilder builder() {
        return new SchedulerClassBuilder();
    }

    public static class SchedulerClassBuilder {
        private TypeElement classElement;
        private List<ScheduledMethodInfo> scheduledMethods;
        private Filer filer;
        private String packageName;
        private String className;

        public SchedulerClassBuilder classElement(TypeElement classElement) {
            this.classElement = classElement;
            return this;
        }

        public SchedulerClassBuilder scheduledMethods(List<ScheduledMethodInfo> scheduledMethods) {
            this.scheduledMethods = scheduledMethods;
            return this;
        }

        public SchedulerClassBuilder filer(Filer filer) {
            this.filer = filer;
            return this;
        }

        public SchedulerClassBuilder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public SchedulerClassBuilder className(String className) {
            this.className = className;
            return this;
        }

        public SchedulerClass build() {
            return new SchedulerClass(classElement, scheduledMethods, filer, packageName, className);
        }
    }
}