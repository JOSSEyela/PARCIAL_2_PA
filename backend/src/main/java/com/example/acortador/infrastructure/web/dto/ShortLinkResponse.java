package com.example.acortador.infrastructure.web.dto;

public record ShortLinkResponse(String id, String originalUrl, String shortCode, String shortUrl) {
}
