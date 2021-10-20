// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.quickserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.List;

@Component
public class WebServerFactory implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private static final Logger log = LoggerFactory.getLogger(WebServerFactory.class);
    private static final String PARAM_LOCAL = "local";
    private static final String PARAM_PORT = "port";

    @Autowired
    private ApplicationArguments arguments;

    @Override
    public void customize(final ConfigurableWebServerFactory factory) {
        // Address.
        InetAddress address = null;
        if (arguments.containsOption(PARAM_LOCAL)) {
            address = InetAddress.getLoopbackAddress();
        }
        // Port.
        Integer port = null;
        if (arguments.containsOption(PARAM_PORT)) {
            final List<String> optionValues = arguments.getOptionValues(PARAM_PORT);
            if (optionValues.size() > 0) {
                final String value = optionValues.get(0);
                try {
                    port = Integer.parseInt(value);
                } catch (final NumberFormatException e) {
                    log.error("Invalid port: {}, reverting to default.", value);
                }
            }
        }
        // Set factory.
        if (address !=  null) {
            log.info("Using address: {}", address);
            factory.setAddress(address);
        }
        if (port != null) {
            log.info("Using port: {}", port);
            factory.setPort(port);
        }
    }
}
