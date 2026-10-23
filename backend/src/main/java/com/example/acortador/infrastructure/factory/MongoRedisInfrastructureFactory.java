package com.example.acortador.infrastructure.factory;

import com.example.acortador.application.factory.LinkInfrastructureFactory;
import com.example.acortador.application.ports.out.ShortCodeGenerator;
import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.application.ports.out.ShortLinkRepository;
import com.example.acortador.infrastructure.cache.redis.ShortLinkCacheRedis;
import com.example.acortador.infrastructure.generator.Base62ShortCodeGenerator;
import com.example.acortador.infrastructure.persistence.mongo.ShortLinkRepositoryMongo;
import com.example.acortador.infrastructure.persistence.mongo.SpringDataShortLinkRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/** Fábrica concreta (producción): MongoDB + Redis. */
@Component
@ConditionalOnProperty(name = "app.infrastructure", havingValue = "mongo-redis", matchIfMissing = true)
public class MongoRedisInfrastructureFactory implements LinkInfrastructureFactory {

    private final ShortLinkRepository repository;
    private final ShortLinkCache cache;
    private final ShortCodeGenerator codeGenerator = new Base62ShortCodeGenerator();

    public MongoRedisInfrastructureFactory(SpringDataShortLinkRepository mongo, StringRedisTemplate redis) {
        this.repository = new ShortLinkRepositoryMongo(mongo);
        this.cache = new ShortLinkCacheRedis(redis);
    }

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
