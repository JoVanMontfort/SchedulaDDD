package com.bart.example.infrastructure.scheduler.ports;

import com.bart.example.infrastructure.scheduler.usecases.CreateSchedulerProcessorUsecase;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({
        "com.bart.example.infrastructure.scheduler.annotations.EnableScheduling",
        "com.bart.example.infrastructure.scheduler.annotations.Scheduled"
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class SchedulerProcessor extends AbstractProcessor {
    private final CreateSchedulerProcessorUsecase createSchedulerProcessorUsecase = CreateSchedulerProcessorUsecase.getInstance();
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "⏰ Processing scheduling annotations: " + annotations.size());
        if (roundEnv.processingOver() || annotations.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "⏰ No scheduling annotations found");
            return false;
        }

        createSchedulerProcessorUsecase.execute(roundEnv, filer, messager);

        return true;
    }
}