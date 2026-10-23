package com.example.acortador.domain.model;

import java.time.Instant;

/**
 * Entidad de dominio: un enlace original y su código corto.
 * No depende de Spring, MongoDB ni Redis.
 */
public record ShortLink(String id, String originalUrl, String shortCode, Instant createdAt) {

    public static ShortLink create(String originalUrl, String shortCode) {
        return new ShortLink(null, originalUrl, shortCode, Instant.now());
    }
}
