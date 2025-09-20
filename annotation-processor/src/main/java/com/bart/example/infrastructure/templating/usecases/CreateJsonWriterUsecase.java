package com.bart.example.infrastructure.templating.usecases;

import com.bart.example.infrastructure.templating.commands.MakeContentJsonWriterCommand;
import com.bart.example.infrastructure.templating.commands.WriteClassCommand;
import com.bart.example.infrastructure.templating.commands.model.WriteClassCommandModel;
import com.bart.example.infrastructure.templating.ports.model.JsonClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateJsonWriterUsecase {
    @Getter
    private final static CreateJsonWriterUsecase instance = new CreateJsonWriterUsecase();

    private static final MakeContentJsonWriterCommand makeContentJsonWriterCommand
            = MakeContentJsonWriterCommand.getInstance();

    private final WriteClassCommand writeClassCommand
            = WriteClassCommand.getInstance();


    public void execute(JsonClass jsonClass) {
        WriteClassCommandModel command = WriteClassCommandModel.builder()
                .filer(jsonClass.getFiler())
                .packageName(jsonClass.getPackageName())
                .className("JsonWriter")
                .content(makeContentJsonWriterCommand.execute(jsonClass))
                .build();
        writeClassCommand.execute(command);
    }

}
