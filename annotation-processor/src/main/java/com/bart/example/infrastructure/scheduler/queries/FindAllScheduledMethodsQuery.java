package com.bart.example.infrastructure.scheduler.queries;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;
import com.bart.example.infrastructure.templating.usecases.ScheduledMethodInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindAllScheduledMethodsQuery {
    @Getter
    private final static FindAllScheduledMethodsQuery instance = new FindAllScheduledMethodsQuery();

    private Types typeUtils;
    private Elements elementUtils;

    public List<ScheduledMethodInfo> execute(RoundEnvironment roundEnv, Messager messager) {
        messager.printMessage(Diagnostic.Kind.NOTE, "🔍 Finding all scheduled methods...");

        List<ScheduledMethodInfo> allMethods = roundEnv.getElementsAnnotatedWith(EnableScheduling.class)
                .stream()
                .filter(this::isValidClassElement)
                .map(element -> (TypeElement) element)
                .filter(classElement -> validateScheduledClass(classElement, messager))
                .flatMap(classElement -> findScheduledMethodsForClass(classElement, roundEnv, messager).stream())
                .collect(Collectors.toList());

        messager.printMessage(Diagnostic.Kind.NOTE,
                "✅ Found " + allMethods.size() + " scheduled methods across " +
                        roundEnv.getElementsAnnotatedWith(EnableScheduling.class).size() + " classes");

        return allMethods;
    }

    private boolean isValidClassElement(Element element) {
        return element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE;
    }

    private boolean validateScheduledClass(TypeElement classElement, Messager messager) {
        // Check if class has a no-arg constructor and is not abstract
        boolean isValid = hasPublicNoArgConstructor(classElement) &&
                !classElement.getModifiers().contains(Modifier.ABSTRACT);

        if (!isValid) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("Class %s must be non-abstract and have a public no-argument constructor",
                            classElement.getSimpleName()),
                    classElement);
        }

        return isValid;
    }

    private boolean hasPublicNoArgConstructor(TypeElement classElement) {
        return classElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                .map(e -> (ExecutableElement) e)
                .anyMatch(constructor ->
                        constructor.getModifiers().contains(Modifier.PUBLIC) &&
                                constructor.getParameters().isEmpty());
    }

    private List<ScheduledMethodInfo> findScheduledMethodsForClass(TypeElement classElement,
                                                                   RoundEnvironment roundEnv,
                                                                   Messager messager) {
        return roundEnv.getElementsAnnotatedWith(Scheduled.class)
                .stream()
                .filter(element -> element.getKind() == ElementKind.METHOD)
                .filter(element -> element.getEnclosingElement().equals(classElement))
                .map(element -> (ExecutableElement) element)
                .filter(methodElement -> validateScheduledMethod(methodElement, messager))
                .map(methodElement -> createScheduledMethodInfo(classElement, methodElement))
                .collect(Collectors.toList());
    }

    private ScheduledMethodInfo createScheduledMethodInfo(TypeElement classElement, ExecutableElement methodElement) {
        Scheduled scheduled = methodElement.getAnnotation(Scheduled.class);
        return new ScheduledMethodInfo(
                classElement,
                methodElement.getSimpleName().toString(),
                scheduled,
                methodElement.getParameters().isEmpty(),
                getMethodQualifiedName(methodElement)
        );
    }

    private String getMethodQualifiedName(ExecutableElement methodElement) {
        TypeElement enclosingClass = (TypeElement) methodElement.getEnclosingElement();
        return String.format("%s.%s",
                elementUtils.getPackageOf(enclosingClass).getQualifiedName(),
                methodElement.getSimpleName());
    }

    private boolean validateScheduledMethod(ExecutableElement methodElement, Messager messager) {
        List<String> errors = new ArrayList<>();

        if (!methodElement.getModifiers().contains(Modifier.PUBLIC)) {
            errors.add("must be public");
        }

        if (!methodElement.getParameters().isEmpty()) {
            errors.add("cannot have parameters");
        }

        if (!returnsVoid(methodElement)) {
            errors.add("must return void");
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.format("Scheduled method %s %s",
                    methodElement.getSimpleName(),
                    String.join(", ", errors));
            messager.printMessage(Diagnostic.Kind.ERROR, errorMessage, methodElement);
            return false;
        }

        return true;
    }

    private boolean returnsVoid(ExecutableElement methodElement) {
        TypeMirror returnType = methodElement.getReturnType();
        TypeMirror voidType = typeUtils.getNoType(TypeKind.VOID);
        return typeUtils.isSameType(returnType, voidType);
    }

    public void setTypeUtils(Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    public void setElementUtils(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }
}