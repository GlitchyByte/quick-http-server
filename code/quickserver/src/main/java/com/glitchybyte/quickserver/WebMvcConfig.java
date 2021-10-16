// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.concurrent.Callable;

@Configuration
@EnableAsync
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private static final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

//    @Override
//    protected void addResourceHandlers(final ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("file:/absolute/path/to/directory/");
//    }
//
//    @Override
//    protected void addViewControllers(final ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/index.html");
//    }

    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        final int processors = Runtime.getRuntime().availableProcessors();
        log.info("Initializing async task executor: {} processors", processors);
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(Math.max(2, processors));
        executor.setQueueCapacity(executor.getMaxPoolSize() * 100);
        executor.setThreadNamePrefix("WebTask-");
        executor.initialize();
        return executor;
    }

    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(getAsyncExecutor())
                .setDefaultTimeout(30_000)
                .registerCallableInterceptors(getCallableProcessingInterceptor());
        super.configureAsyncSupport(configurer);
    }

    private CallableProcessingInterceptor getCallableProcessingInterceptor() {
        return new TimeoutCallableProcessingInterceptor() {
            @Override
            public <T> Object handleTimeout(final NativeWebRequest request, final Callable<T> task) throws Exception {
                log.error("Timeout request: {}", request.getContextPath());
                return super.handleTimeout(request, task);
            }
        };
    }
}
