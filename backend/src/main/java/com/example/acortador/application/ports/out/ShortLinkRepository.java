package com.example.acortador.application.ports.out;

import com.example.acortador.domain.model.ShortLink;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia (puerto). La implementación vive en infrastructure. */
public interface ShortLinkRepository {

    ShortLink save(ShortLink link);

    List<ShortLink> findAll();

    Optional<ShortLink> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
