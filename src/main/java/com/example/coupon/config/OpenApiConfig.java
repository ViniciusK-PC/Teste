package com.example.coupon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Management API")
                        .version("1.0.0")
                        .description("API for managing discount coupons with DDD architecture, soft delete and code sanitization.")
                        .contact(new Contact()
                                .name("Developer")
                                .email("dev@example.com")));
    }
}
