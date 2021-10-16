// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RestController
public final class ServeSourceEndpoint {

    private static final String SOURCE_PARAM = "source";

    private static final Logger log = LoggerFactory.getLogger(ServeSourceEndpoint.class);

    @Autowired
    private ApplicationArguments arguments;

    @CrossOrigin(origins = "*")
    @GetMapping("/**")
    public ResponseEntity<StreamingResponseBody> serveSource(final HttpServletRequest request) {
        final Path source = getSource();
        if ((source == null) || Files.notExists(source)) {
            return ResponseEntity.internalServerError().build();
        }
        final Path path;
        if (Files.isDirectory(source)) {
            final String uri = request.getRequestURI();
            path = source.resolve(uri.length() > 0 ? uri.substring(1) : "");
            if (Files.notExists(path) || !Files.isRegularFile(path)) {
                return ResponseEntity.notFound().build();
            }
        } else {
            path = source;
        }
        final MediaType mediaType = getMimeType(path);
        if (mediaType == null) {
            return ResponseEntity.internalServerError().build();
        }
        final StreamingResponseBody stream = outputStream -> {
            try (final InputStream inputStream = Files.newInputStream(path)) {
                inputStream.transferTo(outputStream);
            }
        };
        // Response.
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(stream);
    }

    private Path getSource() {
        String source = null;
        if (arguments.containsOption(SOURCE_PARAM)) {
            final List<String> sources = arguments.getOptionValues(SOURCE_PARAM);
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
        try {
            final String mimeType = Files.probeContentType(path);
            return MediaType.parseMediaType(mimeType);
        } catch (final IOException | InvalidMediaTypeException e) {
            return null;
        }
    }
}
