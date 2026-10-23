package com.example.acortador.app.config;

import com.example.acortador.application.factory.LinkInfrastructureFactory;
import com.example.acortador.application.ports.out.CacheEventPublisher;
import com.example.acortador.application.service.CacheMonitorService;
import com.example.acortador.application.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuración de arranque: Spring conecta los servicios de aplicación con la
 * familia de adaptadores que produce la fábrica abstracta elegida
 * (propiedad {@code app.infrastructure}). Ocurre una sola vez al iniciar.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ShortLinkService shortLinkService(LinkInfrastructureFactory factory,
                                             @Value("${app.cache.ttl-seconds:30}") long ttlSeconds) {
        return new ShortLinkService(
                factory.createRepository(),
                factory.createCache(),
                factory.createCodeGenerator(),
                Duration.ofSeconds(ttlSeconds));
    }

    @Bean
    public CacheMonitorService cacheMonitorService(LinkInfrastructureFactory factory,
                                                   CacheEventPublisher publisher) {
        return new CacheMonitorService(factory.createCache(), publisher);
    }
}
