package com.fitmefy_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvLoader {

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void init() {
        String absolutePath = "/Users/anuragkumar/Desktop/Projects/fitmefy/fitmefy-backend/.env";
        Dotenv dotenv = Dotenv.configure()
                .directory(new File(absolutePath).getParent())
                .filename(".env")
                .load();

        Map<String, Object> envMap = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            envMap.put(entry.getKey(), entry.getValue());
            // System.out.println("✅ Loaded ENV: " + entry.getKey() + " = " + entry.getValue());
        });

        // Add to Spring Environment so placeholders like ${MAIL_USERNAME} work
        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envMap));
    }
}
