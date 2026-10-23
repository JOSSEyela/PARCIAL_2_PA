package com.example.acortador.application.ports.out;

/** Contrato para notificar en tiempo real el estado de la caché (WebSocket en infrastructure). */
public interface CacheEventPublisher {

    void publishRemaining(long remainingSeconds);
}
