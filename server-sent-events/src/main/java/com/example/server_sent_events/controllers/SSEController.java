package com.example.server_sent_events.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SSEController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/events")
    public SseEmitter streamSSE() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        executor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    emitter.send("SSE MVC - " + System.currentTimeMillis());
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });

        System.out.println("Connected");
        
        return emitter;
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/events")
                        .allowedOrigins("http://localhost:9000");
            }
        };
    }
}
