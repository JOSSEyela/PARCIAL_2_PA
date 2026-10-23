package com.example.acortador.application.ports.out;

import com.example.acortador.domain.model.ShortLink;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/** Contrato de caché para el listado de enlaces (puerto). */
public interface ShortLinkCache {

    Optional<List<ShortLink>> getAll();

    void putAll(List<ShortLink> links, Duration ttl);

    /** Segundos que le quedan a la caché; 0 si no existe o ya expiró. */
    long remainingSeconds();

    void evict();
}
