package com.example.acortador.infrastructure.cache.memory;

import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.domain.model.ShortLink;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Caché en memoria con expiración (pruebas / demo sin Redis). */
public class ShortLinkCacheInMemory implements ShortLinkCache {

    private List<ShortLink> links;
    private Instant expiresAt;

    @Override
    public synchronized Optional<List<ShortLink>> getAll() {
        return remainingSeconds() > 0 ? Optional.of(links) : Optional.empty();
    }

    @Override
    public synchronized void putAll(List<ShortLink> links, Duration ttl) {
        this.links = List.copyOf(links);
        this.expiresAt = Instant.now().plus(ttl);
    }

    @Override
    public synchronized long remainingSeconds() {
        if (expiresAt == null) {
            return 0;
        }
        long millis = Duration.between(Instant.now(), expiresAt).toMillis();
        return millis <= 0 ? 0 : (millis + 999) / 1000;
    }

    @Override
    public synchronized void evict() {
        links = null;
        expiresAt = null;
    }
}
