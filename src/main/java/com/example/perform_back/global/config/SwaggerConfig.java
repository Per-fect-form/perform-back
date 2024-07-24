package com.example.perform_back.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// http://localhost:8080/swagger-ui/index.html#/
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApI(){
        return new OpenAPI()
                .info(new Info()
                .title("Per-form Swagger API")
                .version("v1.0.0")
                .description("Per-form Swagger API")
                );
    }
}
