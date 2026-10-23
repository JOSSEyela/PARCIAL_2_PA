package com.example.acortador.application.dto;

import com.example.acortador.domain.model.ShortLink;

import java.util.List;

/**
 * Resultado del listado: los enlaces, de dónde salieron y cuánto le queda a la caché.
 */
public record LinkListResult(List<ShortLink> links, Source source, long cacheRemainingSeconds) {

    public enum Source { MONGODB, REDIS }
}
