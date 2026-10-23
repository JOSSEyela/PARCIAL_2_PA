package com.example.acortador.infrastructure.factory;

import com.example.acortador.application.factory.LinkInfrastructureFactory;
import com.example.acortador.application.ports.out.ShortCodeGenerator;
import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.application.ports.out.ShortLinkRepository;
import com.example.acortador.infrastructure.cache.memory.ShortLinkCacheInMemory;
import com.example.acortador.infrastructure.generator.Base62ShortCodeGenerator;
import com.example.acortador.infrastructure.persistence.memory.ShortLinkRepositoryInMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Fábrica concreta alternativa: todo en memoria (app.infrastructure=memory). */
@Component
@ConditionalOnProperty(name = "app.infrastructure", havingValue = "memory")
public class InMemoryInfrastructureFactory implements LinkInfrastructureFactory {

    // Instancias únicas: el servicio y el monitor deben compartir la misma caché.
    private final ShortLinkRepository repository = new ShortLinkRepositoryInMemory();
    private final ShortLinkCache cache = new ShortLinkCacheInMemory();
    private final ShortCodeGenerator codeGenerator = new Base62ShortCodeGenerator();

    @Override
    public ShortLinkRepository createRepository() {
        return repository;
    }

    @Override
    public ShortLinkCache createCache() {
        return cache;
    }

    @Override
    public ShortCodeGenerator createCodeGenerator() {
        return codeGenerator;
    }
}
