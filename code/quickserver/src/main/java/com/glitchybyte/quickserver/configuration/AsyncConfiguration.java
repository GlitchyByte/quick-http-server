// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    public static final String TASK_EXECUTOR_DEFAULT = "taskExecutor";
    public static final String TASK_EXECUTOR_DEFAULT_PREFIX = "task";
    public static final String TASK_EXECUTOR_CONTROLLER = "controllerTaskExecutor";
    public static final String TASK_EXECUTOR_CONTROLLER_PREFIX = "controller";

    @Override
    @Bean(name = TASK_EXECUTOR_DEFAULT)
    public Executor getAsyncExecutor() {
        return createTaskExecutor(TASK_EXECUTOR_DEFAULT_PREFIX);
    }

    @Bean(name = TASK_EXECUTOR_CONTROLLER)
    public Executor getControllerAsyncExecutor() {
        return createTaskExecutor(TASK_EXECUTOR_CONTROLLER_PREFIX);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    private Executor createTaskExecutor(final String namePrefix) {
        final int processors = Runtime.getRuntime().availableProcessors();
        final int corePool = 2;
        final int maxPool = Math.max(corePool, processors);
        final int capacity = corePool * 20;
        log.info("Creating '{}' = { core: {}, max: {}, capacity: {} }", namePrefix, corePool, maxPool, capacity);
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePool);
        executor.setMaxPoolSize(maxPool);
        executor.setQueueCapacity(capacity);
        executor.setThreadNamePrefix(namePrefix + "-");
        executor.initialize();
        return executor;
    }
}
