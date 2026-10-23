package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.application.dto.LinkListResult;
import com.example.acortador.application.ports.in.ListShortLinksUseCase;
import com.example.acortador.application.ports.in.ShortenLinkUseCase;
import com.example.acortador.domain.model.ShortLink;
import com.example.acortador.infrastructure.web.dto.LinkListResponse;
import com.example.acortador.infrastructure.web.dto.ShortLinkResponse;
import com.example.acortador.infrastructure.web.dto.ShortenRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/links")
public class ShortLinkController {

    private final ShortenLinkUseCase shortenLink;
    private final ListShortLinksUseCase listShortLinks;
    private final String baseUrl;

    public ShortLinkController(ShortenLinkUseCase shortenLink,
                               ListShortLinksUseCase listShortLinks,
                               @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.shortenLink = shortenLink;
        this.listShortLinks = listShortLinks;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortLinkResponse shorten(@Valid @RequestBody ShortenRequest request) {
        return toResponse(shortenLink.shorten(request.url()));
    }

    @GetMapping
    public LinkListResponse list() {
        LinkListResult result = listShortLinks.listAll();
        return new LinkListResponse(
                result.links().stream().map(this::toResponse).toList(),
                result.source().name(),
                result.cacheRemainingSeconds());
    }

    private ShortLinkResponse toResponse(ShortLink link) {
        return new ShortLinkResponse(link.id(), link.originalUrl(), link.shortCode(),
                baseUrl + "/" + link.shortCode());
    }
}
