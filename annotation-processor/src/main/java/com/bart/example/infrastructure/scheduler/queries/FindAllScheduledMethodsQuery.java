package com.bart.example.infrastructure.scheduler.queries;

import com.bart.example.infrastructure.scheduler.annotations.EnableScheduling;
import com.bart.example.infrastructure.scheduler.annotations.Scheduled;
import com.bart.example.infrastructure.scheduler.usecases.ScheduledMethodInfo;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindAllScheduledMethodsQuery {
    @Getter
    private final static FindAllScheduledMethodsQuery instance = new FindAllScheduledMethodsQuery();

    private Elements elementUtils;
    private Types typeUtils;

    public List<ScheduledMethodInfo> execute(RoundEnvironment roundEnv, Messager messager) {
        List<ScheduledMethodInfo> allMethods = new ArrayList<>();

        // Find all classes annotated with @EnableScheduling
        for (Element element : roundEnv.getElementsAnnotatedWith(EnableScheduling.class)) {
            if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE) {
                TypeElement typeElement = (TypeElement) element;
                if (validateScheduledClass(typeElement, messager)) {
                    List<ScheduledMethodInfo> classMethods = findScheduledMethodsForClass(typeElement, roundEnv, messager);
                    allMethods.addAll(classMethods);
                }
            }
        }

        return allMethods;
    }

    private boolean validateScheduledClass(TypeElement classElement, Messager messager) {
        // Check if class has a no-arg constructor
        boolean hasNoArgConstructor = classElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                .anyMatch(e -> e.getModifiers().contains(Modifier.PUBLIC) &&
                        ((ExecutableElement) e).getParameters().isEmpty());

        if (!hasNoArgConstructor) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Class " + classElement.getSimpleName() + " must have a public no-argument constructor",
                    classElement);
            return false;
        }

        return true;
    }

    private List<ScheduledMethodInfo> findScheduledMethodsForClass(TypeElement classElement,
                                                                   RoundEnvironment roundEnv,
                                                                   Messager messager) {
        List<ScheduledMethodInfo> methods = new ArrayList<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Scheduled.class)) {
            if (element.getKind() == ElementKind.METHOD &&
                    element.getEnclosingElement().equals(classElement)) {

                ExecutableElement methodElement = (ExecutableElement) element;
                Scheduled scheduled = methodElement.getAnnotation(Scheduled.class);

                // Validate method signature
                if (validateScheduledMethod(methodElement, messager)) {
                    methods.add(new ScheduledMethodInfo(
                            classElement,
                            methodElement.getSimpleName().toString(),
                            scheduled,
                            methodElement.getParameters().isEmpty()
                    ));
                }
            }
        }

        return methods;
    }

    private boolean validateScheduledMethod(ExecutableElement methodElement, Messager messager) {
        // Check if method is public
        if (!methodElement.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Scheduled method must be public: " + methodElement.getSimpleName(),
                    methodElement);
            return false;
        }

        // Check if method has parameters (should not)
        if (!methodElement.getParameters().isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Scheduled method cannot have parameters: " + methodElement.getSimpleName(),
                    methodElement);
            return false;
        }

        // Check if method returns void
        TypeMirror returnType = methodElement.getReturnType();
        TypeMirror voidType = typeUtils.getNoType(TypeKind.VOID);
        if (!typeUtils.isSameType(returnType, voidType)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Scheduled method must return void: " + methodElement.getSimpleName(),
                    methodElement);
            return false;
        }

        return true;
    }

    public void setElementUtils(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public void setTypeUtils(Types typeUtils) {
        this.typeUtils = typeUtils;
    }
}