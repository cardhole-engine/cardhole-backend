package com.github.cardhole.session.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class SessionRegistry {

    private final ObjectMapper objectMapper;
    private final Map<WebSocketSession, Session> sessions;

    public SessionRegistry(final ObjectMapper objectMapper) {
        this.sessions = new HashMap<>();
        this.objectMapper = objectMapper;
    }

    public void registerSession(final WebSocketSession session) {
        sessions.put(session,
                Session.builder()
                        .webSocketSession(session)
                        .objectMapper(objectMapper)
                        .build()
        );
    }

    public void removeSession(final WebSocketSession session) {
        sessions.remove(session);
    }

    public Session getSession(final WebSocketSession session) {
        return sessions.get(session);
    }

    public Collection<Session> listSessions() {
        return sessions.values();
    }
}
