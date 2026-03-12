package com.example.recruiting_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Configuration
public class HandlerMappingLogger {

    private static final Logger logger = LoggerFactory.getLogger(HandlerMappingLogger.class);

    @Bean
    public CommandLineRunner logMappings(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        return args -> {
            Map<RequestMappingInfo, ?> map = mapping.getHandlerMethods();
            logger.info("Registered request mappings:");
            map.keySet().forEach(info -> logger.info(info.toString()));
        };
    }
}
