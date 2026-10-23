package com.example.acortador.infrastructure.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataShortLinkRepository extends MongoRepository<ShortLinkDocument, String> {

    Optional<ShortLinkDocument> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<ShortLinkDocument> findAllByOrderByCreatedAtDesc();
}
