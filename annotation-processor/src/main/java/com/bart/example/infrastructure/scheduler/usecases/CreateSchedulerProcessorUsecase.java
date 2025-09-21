package com.bart.example.infrastructure.scheduler.usecases;

import com.bart.example.infrastructure.scheduler.commands.CreateSchedulerBootstrapCommand;
import com.bart.example.infrastructure.scheduler.queries.FindAllScheduledMethodsQuery;
import com.bart.example.infrastructure.templating.usecases.ScheduledMethodInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateSchedulerProcessorUsecase {
    @Getter
    private final static CreateSchedulerProcessorUsecase instance = new CreateSchedulerProcessorUsecase();

    private static final FindAllScheduledMethodsQuery findAllScheduledMethodsQuery
            = FindAllScheduledMethodsQuery.getInstance();
    private static final CreateSchedulerBootstrapCommand createSchedulerBootstrapCommand
            = CreateSchedulerBootstrapCommand.getInstance();

    public void execute(RoundEnvironment roundEnv, Filer filer, Messager messager) {
        // Check if dependencies are initialized
        if (findAllScheduledMethodsQuery.getTypeUtils() == null) {
            messager.printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "TypeUtils not initialized in FindAllScheduledMethodsQuery");
            return;
        }

        List<ScheduledMethodInfo> scheduledMethods = findAllScheduledMethodsQuery.execute(roundEnv, messager);
        createSchedulerBootstrapCommand.execute(filer, scheduledMethods, messager);
    }

    public void setElementUtils(Elements elementUtils) {
        if (findAllScheduledMethodsQuery != null) {
            findAllScheduledMethodsQuery.setElementUtils(elementUtils);
        }
    }

    public void setTypeUtils(Types typeUtils) {
        if (findAllScheduledMethodsQuery != null) {
            findAllScheduledMethodsQuery.setTypeUtils(typeUtils);
        }
    }
}