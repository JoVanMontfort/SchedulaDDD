package com.bart.example.infrastructure.scheduler.commands;

import com.bart.example.infrastructure.scheduler.model.SchedulerClass;
import com.bart.example.infrastructure.templating.ports.TemplatingPort;
import com.bart.example.infrastructure.templating.usecases.ScheduledMethodInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateSchedulerBootstrapCommand {
    @Getter
    private final static CreateSchedulerBootstrapCommand instance = new CreateSchedulerBootstrapCommand();
    private static final TemplatingPort templatingPort = TemplatingPort.getInstance();

    public void execute(Filer filer, List<ScheduledMethodInfo> scheduledMethods, Messager messager) {
        if (scheduledMethods.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "⏰ No scheduled methods found to process");
            return;
        }

        messager.printMessage(Diagnostic.Kind.NOTE,
                String.format("⏰ Processing %d scheduled methods across %d classes",
                        scheduledMethods.size(),
                        scheduledMethods.stream().map(ScheduledMethodInfo::classElement).distinct().count()));

        Map<TypeElement, List<ScheduledMethodInfo>> methodsByClass = scheduledMethods.stream()
                .collect(Collectors.groupingBy(ScheduledMethodInfo::classElement));

        methodsByClass.forEach((classElement, methods) -> {
            try {
                processClassScheduler(filer, classElement, methods, messager);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Failed to generate scheduler for class %s: %s",
                                classElement.getSimpleName(), e.getMessage()),
                        classElement);
            }
        });

        messager.printMessage(Diagnostic.Kind.NOTE,
                String.format("✅ Successfully generated %d scheduler classes", methodsByClass.size()));
    }

    private void processClassScheduler(Filer filer, TypeElement classElement,
                                       List<ScheduledMethodInfo> methods, Messager messager) {
        String packageName = getPackageName(classElement);
        String className = classElement.getSimpleName().toString();
        String schedulerClassName = className + "Scheduler";

        SchedulerClass schedulerClass = SchedulerClass.builder()
                .classElement(classElement)
                .scheduledMethods(methods)
                .filer(filer)
                .packageName(packageName)
                .className(schedulerClassName)
                .build();

        templatingPort.generateSchedulerClass(schedulerClass);

        messager.printMessage(Diagnostic.Kind.NOTE,
                String.format("✅ Generated scheduler: %s.%s (%d methods)",
                        packageName, schedulerClassName, methods.size()),
                classElement);
    }

    private String getPackageName(TypeElement classElement) {
        Element enclosingElement = classElement.getEnclosingElement();
        while (enclosingElement != null && !(enclosingElement instanceof PackageElement)) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }

        if (enclosingElement instanceof PackageElement) {
            return ((PackageElement) enclosingElement).getQualifiedName().toString();
        }

        return ""; // Default package
    }

    // Additional utility method for better package handling
    private boolean isValidPackage(String packageName) {
        return packageName != null && !packageName.trim().isEmpty();
    }

    // Validation method to ensure class can have scheduler generated
    private boolean canGenerateScheduler(TypeElement classElement, List<ScheduledMethodInfo> methods, Messager messager) {
        if (methods.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    String.format("Class %s has @EnableScheduling but no @Scheduled methods",
                            classElement.getSimpleName()),
                    classElement);
            return false;
        }

        String packageName = getPackageName(classElement);
        if (!isValidPackage(packageName)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("Class %s must be in a valid package to generate scheduler",
                            classElement.getSimpleName()),
                    classElement);
            return false;
        }

        return true;
    }
}