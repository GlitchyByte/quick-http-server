// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver.endpoint;

import com.glitchybyte.quickserver.configuration.AsyncConfiguration;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ServeSourceEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ServeSourceEndpoint.class);
    private static final String PARAM_SOURCE = "source";

    @Autowired
    private ApplicationArguments arguments;

    @CrossOrigin(origins = "*")
    @Async(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
    @GetMapping("/**")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> serveSource(final HttpServletRequest request) {
        final Path source = getSource();
        if ((source == null) || Files.notExists(source)) {
            log.warn("No source defined or source not found!");
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        }
        final Path path;
        if (Files.isDirectory(source)) {
            final String uri = request.getRequestURI();
            path = source.resolve(uri.length() > 0 ? uri.substring(1) : "");
            if (Files.notExists(path) || !Files.isRegularFile(path)) {
                return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
            }
        } else {
            path = source;
        }
        final MediaType mediaType = getMimeType(path);
        if (mediaType == null) {
            log.warn("MediaType not found!");
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        }
        final StreamingResponseBody stream = outputStream -> {
            try (final InputStream inputStream = Files.newInputStream(path)) {
                inputStream.transferTo(outputStream);
            }
        };
        // Response.
        return CompletableFuture.completedFuture(
                ResponseEntity.ok()
                        .cacheControl(CacheControl.noCache())
                        .contentType(mediaType)
                        .body(stream)
        );
    }

    private Path getSource() {
        String source = null;
        if (arguments.containsOption(PARAM_SOURCE)) {
            final List<String> sources = arguments.getOptionValues(PARAM_SOURCE);
            if (sources.size() > 0) {
                source = sources.get(0);
            }
        }
        if (source == null) {
            return null;
        }
        return Paths.get(source).toAbsolutePath().normalize();
    }

    private MediaType getMimeType(final Path path) {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        final String mimeType;
        try {
            final Tika tika = new Tika();
            mimeType = tika.detect(path);
        } catch (final IOException e) {
            return null;
        }
        return MediaType.parseMediaType(mimeType);
    }
}
