// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring.template;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class FileEndpointBase {

    private static final Logger log = LoggerFactory.getLogger(FileEndpointBase.class);
    protected static final CompletableFuture<ResponseEntity<StreamingResponseBody>> NOT_FOUND =
            CompletableFuture.completedFuture(ResponseEntity.notFound().build());

    private final CompletableFuture<ResponseEntity<StreamingResponseBody>> redirectToRoot;
    private final Path sourceRootPath;
    private final CacheControl cacheControl;

    /**
     * Sets up the file endpoint.
     *
     * @param baseUri The base uri of requests. (e.g, "/web")
     * @param sourceRootPath The local file system source root.
     * @param cacheControl CacheControl to use for successful responses.
     */
    protected FileEndpointBase(final String baseUri, final Path sourceRootPath, final CacheControl cacheControl) {
        redirectToRoot = CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(baseUri + "/"))
                        .build()
        );
        this.sourceRootPath = sourceRootPath;
        this.cacheControl = cacheControl;
    }

    public CompletableFuture<ResponseEntity<StreamingResponseBody>> serveFileWithIndex(final String requestedUri) {
        if (requestedUri.isEmpty()) {
            return redirectToRoot;
        }
        if (requestedUri.equals("/")) {
            return serveFile("/index.html");
        }
        return serveFile(requestedUri);
    }

    public CompletableFuture<ResponseEntity<StreamingResponseBody>> serveFile(final String requestedUri) {
        final Path localPath = sourceRootPath.resolve(requestedUri.substring(1));
        return serveLocalPath(localPath);
    }

    protected abstract CompletableFuture<ResponseEntity<StreamingResponseBody>> serveLocalPath(final Path localPath);

    protected CompletableFuture<ResponseEntity<StreamingResponseBody>> streamingResponse(final Path localPath, final MediaType mediaType) {
        final StreamingResponseBody stream = outputStream -> {
            try (final InputStream inputStream = Files.newInputStream(localPath)) {
                inputStream.transferTo(outputStream);
            }
        };
        return CompletableFuture.completedFuture(
                ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .contentType(mediaType)
                        .body(stream)
        );
    }

    protected MediaType getMediaType(final Path path) {
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
