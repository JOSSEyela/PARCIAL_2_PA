package com.example.acortador.application.service;

import com.example.acortador.application.ports.in.PublishCacheStatusUseCase;
import com.example.acortador.application.ports.out.CacheEventPublisher;
import com.example.acortador.application.ports.out.ShortLinkCache;

/**
 * Consulta el TTL de la caché y lo publica. Mientras la caché está viva se envía
 * cada tick; cuando expira se envía un único 0 para que el cliente cierre la alerta.
 */
public class CacheMonitorService implements PublishCacheStatusUseCase {

    private final ShortLinkCache cache;
    private final CacheEventPublisher publisher;
    private boolean wasActive = false;

    public CacheMonitorService(ShortLinkCache cache, CacheEventPublisher publisher) {
        this.cache = cache;
        this.publisher = publisher;
    }

    @Override
    public synchronized void publishCacheStatus() {
        long remaining = cache.remainingSeconds();
        if (remaining > 0) {
            publisher.publishRemaining(remaining);
            wasActive = true;
        } else if (wasActive) {
            publisher.publishRemaining(0);
            wasActive = false;
        }
    }
}
