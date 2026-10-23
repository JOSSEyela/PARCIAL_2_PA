package com.example.acortador.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(@NotBlank String url) {
}
