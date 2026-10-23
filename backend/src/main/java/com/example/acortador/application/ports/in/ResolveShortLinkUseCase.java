package com.example.acortador.application.ports.in;

public interface ResolveShortLinkUseCase {

    /** Devuelve la URL original asociada al código corto. */
    String resolve(String shortCode);
}
