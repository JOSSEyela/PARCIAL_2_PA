package com.example.acortador.infrastructure.cache.redis;

import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.domain.model.ShortLink;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/** Adaptador: guarda el listado de enlaces en Redis como JSON con TTL. */
public class ShortLinkCacheRedis implements ShortLinkCache {

    static final String KEY = "short-links:all";

    private final StringRedisTemplate redis;
    private final JsonMapper json = JsonMapper.builder().build();

    public ShortLinkCacheRedis(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Optional<List<ShortLink>> getAll() {
        String value = redis.opsForValue().get(KEY);
        if (value == null) {
            return Optional.empty();
        }
        List<CachedLink> cached = json.readValue(value, new TypeReference<>() {});
        return Optional.of(cached.stream().map(CachedLink::toDomain).toList());
    }

    @Override
    public void putAll(List<ShortLink> links, Duration ttl) {
        List<CachedLink> cached = links.stream().map(CachedLink::fromDomain).toList();
        redis.opsForValue().set(KEY, json.writeValueAsString(cached), ttl);
    }

    @Override
    public long remainingSeconds() {
        Long ttl = redis.getExpire(KEY, TimeUnit.SECONDS);
        return ttl == null || ttl < 0 ? 0 : ttl;
    }

    @Override
    public void evict() {
        redis.delete(KEY);
    }

    /** Forma serializable del enlace dentro de Redis. */
    record CachedLink(String id, String originalUrl, String shortCode, long createdAtMillis) {

        static CachedLink fromDomain(ShortLink l) {
            long millis = l.createdAt() == null ? 0 : l.createdAt().toEpochMilli();
            return new CachedLink(l.id(), l.originalUrl(), l.shortCode(), millis);
        }

        ShortLink toDomain() {
            return new ShortLink(id, originalUrl, shortCode, Instant.ofEpochMilli(createdAtMillis));
        }
    }
}
