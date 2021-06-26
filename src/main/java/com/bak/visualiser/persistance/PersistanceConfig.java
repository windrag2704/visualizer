package com.bak.visualiser.persistance;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;

@Configuration
public class PersistanceConfig {
    private final Path path = new File("./projects").toPath();
    @Bean
    public Path getWorkDir() {
        return path;
    }
}
