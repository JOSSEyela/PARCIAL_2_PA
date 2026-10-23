package com.example.acortador.infrastructure.persistence.mongo;

import com.example.acortador.application.ports.out.ShortLinkRepository;
import com.example.acortador.domain.model.ShortLink;

import java.util.List;
import java.util.Optional;

/** Adaptador: implementa el contrato ShortLinkRepository usando MongoDB. */
public class ShortLinkRepositoryMongo implements ShortLinkRepository {

    private final SpringDataShortLinkRepository mongo;

    public ShortLinkRepositoryMongo(SpringDataShortLinkRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public ShortLink save(ShortLink link) {
        return mongo.save(ShortLinkDocument.fromDomain(link)).toDomain();
    }

    @Override
    public List<ShortLink> findAll() {
        return mongo.findAllByOrderByCreatedAtDesc().stream().map(ShortLinkDocument::toDomain).toList();
    }

    @Override
    public Optional<ShortLink> findByShortCode(String shortCode) {
        return mongo.findByShortCode(shortCode).map(ShortLinkDocument::toDomain);
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        return mongo.existsByShortCode(shortCode);
    }
}
