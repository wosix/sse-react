package com.example.server_sent_events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/events")
                        .allowedOrigins("http://localhost:9000")
                        .allowCredentials(true)
                        .allowedHeaders("*");
                registry.addMapping("/action")
                        .allowedOrigins("http://localhost:9000")
                        .allowedMethods("POST")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }
}
