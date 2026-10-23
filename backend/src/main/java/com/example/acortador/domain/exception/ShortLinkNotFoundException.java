package com.example.acortador.domain.exception;

public class ShortLinkNotFoundException extends RuntimeException {

    public ShortLinkNotFoundException(String shortCode) {
        super("No existe un enlace con el código: " + shortCode);
    }
}
