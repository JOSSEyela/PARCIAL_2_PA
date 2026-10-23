package com.example.acortador.application.ports.in;

public interface PublishCacheStatusUseCase {

    /** Publica a los clientes el tiempo restante de la caché. */
    void publishCacheStatus();
}
