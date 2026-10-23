package com.example.acortador.application.ports.in;

import com.example.acortador.domain.model.ShortLink;

public interface ShortenLinkUseCase {

    ShortLink shorten(String originalUrl);
}
