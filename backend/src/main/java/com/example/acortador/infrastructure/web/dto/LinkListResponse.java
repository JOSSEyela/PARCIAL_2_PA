package com.example.acortador.infrastructure.web.dto;

import java.util.List;

/** source = "MONGODB" o "REDIS"; cacheRemainingSeconds = segundos de vida de la caché. */
public record LinkListResponse(List<ShortLinkResponse> links, String source, long cacheRemainingSeconds) {
}
