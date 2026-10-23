package com.example.acortador.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de arranque. Spring escanea todo el paquete raíz para encontrar
 * adaptadores (infrastructure) y la configuración que los conecta con los puertos.
 */
@SpringBootApplication(scanBasePackages = "com.example.acortador")
@EnableMongoRepositories(basePackages = "com.example.acortador.infrastructure.persistence.mongo")
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
