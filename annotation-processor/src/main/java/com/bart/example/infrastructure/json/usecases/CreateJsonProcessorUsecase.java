package com.bart.example.infrastructure.json.usecases;

import com.bart.example.infrastructure.json.commands.CreateJsonReaderCommand;
import com.bart.example.infrastructure.json.commands.CreateJsonWriterCommand;
import com.bart.example.infrastructure.json.queries.FindAllJsonAnnotatedObjectsQuery;
import com.bart.example.infrastructure.templating.ports.model.JsonBaseClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateJsonProcessorUsecase {
    @Getter
    private final static CreateJsonProcessorUsecase instance = new CreateJsonProcessorUsecase();

    private static FindAllJsonAnnotatedObjectsQuery findAllJsonAnnotatedObjectsQuery
            = FindAllJsonAnnotatedObjectsQuery.getInstance();
    private static CreateJsonWriterCommand createJsonWriterCommand
            = CreateJsonWriterCommand.getInstance();
    private static CreateJsonReaderCommand createJsonReaderCommand
            = CreateJsonReaderCommand.getInstance();

    public void execute(RoundEnvironment roundEnv, Filer filer) {
        List<JsonBaseClass> baseClasses = findAllJsonAnnotatedObjectsQuery.execute(roundEnv);
        createJsonWriterCommand.execute(filer, baseClasses);
        createJsonReaderCommand.execute(filer, baseClasses);
    }

}