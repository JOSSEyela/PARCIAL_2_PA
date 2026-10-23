package com.example.acortador.infrastructure.persistence.memory;

import com.example.acortador.application.ports.out.ShortLinkRepository;
import com.example.acortador.domain.model.ShortLink;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Adaptador alternativo (pruebas / demo sin MongoDB). */
public class ShortLinkRepositoryInMemory implements ShortLinkRepository {

    private final Map<String, ShortLink> byCode = new ConcurrentHashMap<>();

    @Override
    public ShortLink save(ShortLink link) {
        ShortLink stored = link.id() != null ? link
                : new ShortLink(UUID.randomUUID().toString(), link.originalUrl(), link.shortCode(), link.createdAt());
        byCode.put(stored.shortCode(), stored);
        return stored;
    }

    @Override
    public List<ShortLink> findAll() {
        return byCode.values().stream()
                .sorted(Comparator.comparing(ShortLink::createdAt).reversed())
                .toList();
    }

    @Override
    public Optional<ShortLink> findByShortCode(String shortCode) {
        return Optional.ofNullable(byCode.get(shortCode));
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        return byCode.containsKey(shortCode);
    }
}
