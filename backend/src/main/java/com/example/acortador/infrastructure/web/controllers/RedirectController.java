package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.application.ports.in.ResolveShortLinkUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/** Hace funcional el enlace acortado: GET /{codigo} redirige a la URL original. */
@RestController
public class RedirectController {

    private final ResolveShortLinkUseCase resolveShortLink;

    public RedirectController(ResolveShortLinkUseCase resolveShortLink) {
        this.resolveShortLink = resolveShortLink;
    }

    @GetMapping("/{code:[0-9a-zA-Z]{4,12}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String target = resolveShortLink.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(target).toASCIIString())
                .build();
    }
}
