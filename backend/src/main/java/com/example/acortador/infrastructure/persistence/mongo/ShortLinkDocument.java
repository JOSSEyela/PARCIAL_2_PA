package com.example.acortador.infrastructure.persistence.mongo;

import com.example.acortador.domain.model.ShortLink;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "short_links")
public class ShortLinkDocument {

    @Id
    private String id;
    private String originalUrl;
    @Indexed(unique = true)
    private String shortCode;
    private Instant createdAt;

    public ShortLinkDocument() {
    }

    public static ShortLinkDocument fromDomain(ShortLink link) {
        ShortLinkDocument doc = new ShortLinkDocument();
        doc.id = link.id();
        doc.originalUrl = link.originalUrl();
        doc.shortCode = link.shortCode();
        doc.createdAt = link.createdAt();
        return doc;
    }

    public ShortLink toDomain() {
        return new ShortLink(id, originalUrl, shortCode, createdAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
