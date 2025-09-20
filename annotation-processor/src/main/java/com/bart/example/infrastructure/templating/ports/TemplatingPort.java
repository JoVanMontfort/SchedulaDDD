package com.bart.example.infrastructure.templating.ports;

import com.bart.example.infrastructure.scheduler.model.SchedulerClass;
import com.bart.example.infrastructure.scheduler.usecases.CreateSchedulerProcessorUsecase;
import com.bart.example.infrastructure.templating.ports.model.DependencyClasses;
import com.bart.example.infrastructure.templating.ports.model.DispatchClasses;
import com.bart.example.infrastructure.templating.ports.model.EndpointClasses;
import com.bart.example.infrastructure.templating.ports.model.JsonClass;
import com.bart.example.infrastructure.templating.usecases.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplatingPort {
    @Getter
    private final static TemplatingPort instance = new TemplatingPort();

    private final CreateEndpointClassesUsecase createEndpointClassesUsecase = CreateEndpointClassesUsecase.getInstance();
    private final CreateDispatchClassesUsecase createDispatchClassesUsecase = CreateDispatchClassesUsecase.getInstance();
    private final CreateDependencyInjectorUsecase createDependencyInjectorUsecase = CreateDependencyInjectorUsecase.getInstance();
    private final CreateJsonWriterUsecase createJsonWriterUsecase = CreateJsonWriterUsecase.getInstance();
    private final CreateJsonReaderUsecase createJsonReaderUsecase = CreateJsonReaderUsecase.getInstance();
    private final CreateSchedulerProcessorUsecase createSchedulerClassUsecase = CreateSchedulerProcessorUsecase.getInstance();

    public void generateEndpointClasses(EndpointClasses endpointClasses) {
        createEndpointClassesUsecase.execute(endpointClasses);
    }

    public void generateDispatchClasses(DispatchClasses dispatchClasses) {
        createDispatchClassesUsecase.execute(dispatchClasses);
    }

    public void generateInjectorClasses(DependencyClasses dependencyClasses) {
        createDependencyInjectorUsecase.execute(dependencyClasses);
    }

    public void generateJsonWriter(JsonClass jsonClass) {
        createJsonWriterUsecase.execute(jsonClass);
    }

    public void generateJsonReader(JsonClass jsonClass) {
        createJsonReaderUsecase.execute(jsonClass);
    }

    public void generateSchedulerClass(SchedulerClass schedulerClass) {
        createSchedulerClassUsecase.execute(schedulerClass);
    }
}