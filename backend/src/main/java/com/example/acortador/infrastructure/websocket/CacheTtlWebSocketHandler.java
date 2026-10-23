package com.example.acortador.infrastructure.websocket;

import com.example.acortador.application.ports.out.CacheEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/** Adaptador: implementa CacheEventPublisher enviando el TTL a todos los clientes WebSocket. */
@Component
public class CacheTtlWebSocketHandler extends TextWebSocketHandler implements CacheEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CacheTtlWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    public void publishRemaining(long remainingSeconds) {
        TextMessage message = new TextMessage(
                "{\"type\":\"CACHE_TTL\",\"remainingSeconds\":" + remainingSeconds
                        + ",\"active\":" + (remainingSeconds > 0) + "}");
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                log.debug("No se pudo enviar a la sesión {}", session.getId(), e);
                sessions.remove(session);
            }
        }
    }
}
