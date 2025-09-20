package com.bart.example.infrastructure.json.ports;

import com.bart.example.infrastructure.json.usecases.CreateJsonProcessorUsecase;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"com.bart.example.infrastructure.json.annotations.JsonObject"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class JsonProcessor extends AbstractProcessor {
    private final CreateJsonProcessorUsecase createJsonProcessorUsecase = CreateJsonProcessorUsecase.getInstance();
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
        messager.printMessage(Diagnostic.Kind.NOTE, "🔍 Processing round with annotations: " + annotations.size());
        if (roundEnv.processingOver() || annotations.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "X Nothing to see here ");
            return false;
        }

        createJsonProcessorUsecase.execute(roundEnv, filer);

        return true;
    }

}