package com.sparta.logistics.notification.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/"))) 
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Notification Service API")
                        .version("v1.0"));
    }
}