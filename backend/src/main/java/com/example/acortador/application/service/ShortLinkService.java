package com.example.acortador.application.service;

import com.example.acortador.application.dto.LinkListResult;
import com.example.acortador.application.dto.LinkListResult.Source;
import com.example.acortador.application.ports.in.ListShortLinksUseCase;
import com.example.acortador.application.ports.in.ResolveShortLinkUseCase;
import com.example.acortador.application.ports.in.ShortenLinkUseCase;
import com.example.acortador.application.ports.out.ShortCodeGenerator;
import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.application.ports.out.ShortLinkRepository;
import com.example.acortador.domain.exception.InvalidUrlException;
import com.example.acortador.domain.exception.ShortLinkNotFoundException;
import com.example.acortador.domain.model.ShortLink;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio. Solo usa contratos (puertos); no sabe si detrás hay
 * MongoDB, Redis o memoria.
 */
public class ShortLinkService implements ShortenLinkUseCase, ListShortLinksUseCase, ResolveShortLinkUseCase {

    private static final int MAX_CODE_ATTEMPTS = 10;

    private final ShortLinkRepository repository;
    private final ShortLinkCache cache;
    private final ShortCodeGenerator codeGenerator;
    private final Duration cacheTtl;

    public ShortLinkService(ShortLinkRepository repository, ShortLinkCache cache,
                            ShortCodeGenerator codeGenerator, Duration cacheTtl) {
        this.repository = repository;
        this.cache = cache;
        this.codeGenerator = codeGenerator;
        this.cacheTtl = cacheTtl;
    }

    @Override
    public ShortLink shorten(String originalUrl) {
        String url = normalizeAndValidate(originalUrl);
        ShortLink saved = repository.save(ShortLink.create(url, nextFreeCode()));
        // El listado cacheado quedó desactualizado: la próxima consulta vuelve a MongoDB.
        cache.evict();
        return saved;
    }

    @Override
    public LinkListResult listAll() {
        Optional<List<ShortLink>> cached = cache.getAll();
        if (cached.isPresent()) {
            return new LinkListResult(cached.get(), Source.REDIS, cache.remainingSeconds());
        }
        List<ShortLink> links = repository.findAll();
        cache.putAll(links, cacheTtl);
        return new LinkListResult(links, Source.MONGODB, cacheTtl.toSeconds());
    }

    @Override
    public String resolve(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortLink::originalUrl)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortCode));
    }

    private String nextFreeCode() {
        for (int i = 0; i < MAX_CODE_ATTEMPTS; i++) {
            String code = codeGenerator.generate();
            if (!repository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("No se pudo generar un código único");
    }

    private static String normalizeAndValidate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new InvalidUrlException("El enlace no puede estar vacío");
        }
        String url = rawUrl.trim();
        if (!url.matches("(?i)^https?://.*")) {
            url = "https://" + url;
        }
        try {
            URI uri = URI.create(url);
            if (uri.getHost() == null || !uri.getHost().contains(".")) {
                throw new InvalidUrlException("El enlace no tiene un dominio válido");
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidUrlException("El enlace no es una URL válida");
        }
        return url;
    }
}
