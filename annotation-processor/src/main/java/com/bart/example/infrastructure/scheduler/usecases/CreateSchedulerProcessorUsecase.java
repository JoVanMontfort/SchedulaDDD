package com.bart.example.infrastructure.scheduler.usecases;

import com.bart.example.infrastructure.scheduler.commands.MakeSchedulerContentCommand;
import com.bart.example.infrastructure.scheduler.model.SchedulerClass;
import com.bart.example.infrastructure.templating.commands.WriteClassCommand;
import com.bart.example.infrastructure.templating.commands.model.WriteClassCommandModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateSchedulerProcessorUsecase {
    @Getter
    private final static CreateSchedulerProcessorUsecase instance = new CreateSchedulerProcessorUsecase();

    private static final MakeSchedulerContentCommand makeSchedulerContentUsecase = MakeSchedulerContentCommand.getInstance();
    private static final WriteClassCommand writeClassCommand = WriteClassCommand.getInstance();

    public void execute(SchedulerClass schedulerClass) {
        WriteClassCommandModel command = WriteClassCommandModel.builder()
                .filer(schedulerClass.filer())
                .packageName(schedulerClass.packageName())
                .className(schedulerClass.className())
                .content(makeSchedulerContentUsecase.execute(schedulerClass))
                .build();
        writeClassCommand.execute(command);
    }
}