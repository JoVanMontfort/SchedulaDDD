package com.bart.example.infrastructure.scheduler.commands;

import com.bart.example.infrastructure.scheduler.model.SchedulerClass;
import com.bart.example.infrastructure.scheduler.usecases.ScheduledMethodInfo;
import com.bart.example.infrastructure.templating.ports.TemplatingPort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateSchedulerBootstrapCommand {
    @Getter
    private final static CreateSchedulerBootstrapCommand instance = new CreateSchedulerBootstrapCommand();
    private static final TemplatingPort templatingPort = TemplatingPort.getInstance();

    public void execute(Filer filer, List<ScheduledMethodInfo> scheduledMethods, Messager messager) {
        if (scheduledMethods.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "No scheduled methods found to process");
            return;
        }

        // Group methods by class and create a scheduler for each class
        scheduledMethods.stream()
                .collect(java.util.stream.Collectors.groupingBy(ScheduledMethodInfo::classElement))
                .forEach((classElement, methods) -> {
                    String packageName = getPackageName(classElement);
                    String className = classElement.getSimpleName().toString();

                    SchedulerClass schedulerClass = SchedulerClass.builder()
                            .classElement(classElement)
                            .scheduledMethods(methods)
                            .filer(filer)
                            .packageName(packageName)
                            .className(className + "Scheduler")
                            .build();

                    templatingPort.generateSchedulerClass(schedulerClass);

                    messager.printMessage(Diagnostic.Kind.NOTE,
                            "Generated scheduler: " + packageName + "." + schedulerClass.className(),
                            classElement);
                });
    }

    private String getPackageName(TypeElement classElement) {
        javax.lang.model.element.PackageElement packageElement =
                (javax.lang.model.element.PackageElement) classElement.getEnclosingElement();
        return packageElement.getQualifiedName().toString();
    }
}