// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private static final Logger log = LoggerFactory.getLogger(WebMvcConfiguration.class);

//    @Override
//    protected void addResourceHandlers(final ResourceHandlerRegistry registry) {
//        // Serves static files.
//        registry.addResourceHandler("/**")
//                .addResourceLocations("file:/absolute/path/to/directory/");
//    }
//
//    @Override
//    protected void addViewControllers(final ViewControllerRegistry registry) {
//        // Redirects root to index.html.
//        registry.addViewController("/")
//                .setViewName("forward:/index.html");
//    }

    @Autowired
    private Executor taskExecutor;

    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor((AsyncTaskExecutor) taskExecutor)
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
