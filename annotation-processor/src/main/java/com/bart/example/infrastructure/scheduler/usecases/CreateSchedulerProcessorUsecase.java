package com.bart.example.infrastructure.scheduler.usecases;

import com.bart.example.infrastructure.scheduler.commands.CreateSchedulerBootstrapCommand;
import com.bart.example.infrastructure.scheduler.queries.FindAllScheduledMethodsQuery;
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

    private static FindAllScheduledMethodsQuery findAllScheduledMethodsQuery
            = FindAllScheduledMethodsQuery.getInstance();
    private static CreateSchedulerBootstrapCommand createSchedulerBootstrapCommand
            = CreateSchedulerBootstrapCommand.getInstance();

    public void execute(RoundEnvironment roundEnv, Filer filer, Messager messager) {
        List<ScheduledMethodInfo> scheduledMethods = findAllScheduledMethodsQuery.execute(roundEnv, messager);
        createSchedulerBootstrapCommand.execute(filer, scheduledMethods, messager);
    }

    public void setElementUtils(Elements elementUtils) {
        findAllScheduledMethodsQuery.setElementUtils(elementUtils);
    }

    public void setTypeUtils(Types typeUtils) {
        findAllScheduledMethodsQuery.setTypeUtils(typeUtils);
    }
}