package com.example.acortador.infrastructure.websocket;

import com.example.acortador.application.ports.in.PublishCacheStatusUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Cada segundo pide a la aplicación que publique el TTL de la caché. */
@Component
public class CacheTtlScheduler {

    private final PublishCacheStatusUseCase publishCacheStatus;

    public CacheTtlScheduler(PublishCacheStatusUseCase publishCacheStatus) {
        this.publishCacheStatus = publishCacheStatus;
    }

    @Scheduled(fixedRate = 1000)
    public void tick() {
        publishCacheStatus.publishCacheStatus();
    }
}
