package com.example.springboot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "Authorization";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT");
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securityScheme.getName(), securityScheme)
                )
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(securityScheme.getName()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Spring Boot Server API")
                .description("Spring Boot Server API")
                .version("0.0.1");
    }


    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}

