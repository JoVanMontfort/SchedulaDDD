package com.bart.example.infrastructure.scheduler.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Scheduled {
    String cron() default "";

    long fixedDelay() default -1;

    long fixedRate() default -1;

    long initialDelay() default 0;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}