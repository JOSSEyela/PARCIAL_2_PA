package com.example.acortador.application.service;

import com.example.acortador.application.dto.LinkListResult;
import com.example.acortador.application.dto.LinkListResult.Source;
import com.example.acortador.application.factory.LinkInfrastructureFactory;
import com.example.acortador.domain.exception.InvalidUrlException;
import com.example.acortador.domain.model.ShortLink;
import com.example.acortador.infrastructure.factory.InMemoryInfrastructureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortLinkServiceTest {

    private ShortLinkService service;

    @BeforeEach
    void setUp() {
        LinkInfrastructureFactory factory = new InMemoryInfrastructureFactory();
        service = new ShortLinkService(factory.createRepository(), factory.createCache(),
                factory.createCodeGenerator(), Duration.ofSeconds(30));
    }

    @Test
    void shortenAddsSchemeAndResolves() {
        ShortLink link = service.shorten("google.com");
        assertEquals("https://google.com", link.originalUrl());
        assertEquals(7, link.shortCode().length());
        assertEquals("https://google.com", service.resolve(link.shortCode()));
    }

    @Test
    void secondListComesFromCache() {
        service.shorten("https://example.com");
        LinkListResult first = service.listAll();
        LinkListResult second = service.listAll();
        assertEquals(Source.MONGODB, first.source());
        assertEquals(Source.REDIS, second.source());
        assertEquals(1, second.links().size());
        assertTrue(second.cacheRemainingSeconds() > 0 && second.cacheRemainingSeconds() <= 30);
    }

    @Test
    void shortenInvalidatesCache() {
        service.listAll();
        service.shorten("https://example.com");
        assertEquals(Source.MONGODB, service.listAll().source());
    }

    @Test
    void rejectsInvalidUrl() {
        assertThrows(InvalidUrlException.class, () -> service.shorten("  "));
        assertThrows(InvalidUrlException.class, () -> service.shorten("nodomain"));
    }
}
