package com.github.cardhole.session.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cardhole.session.domain.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class SessionRegistry {

    private final ObjectMapper outputObjectMapper;
    private final Map<WebSocketSession, Session> sessions;

    public SessionRegistry(@Qualifier("outputObjectMapper") final ObjectMapper outputObjectMapper) {
        this.sessions = new HashMap<>();
        this.outputObjectMapper = outputObjectMapper;
    }

    public void registerSession(final WebSocketSession session) {
        sessions.put(session,
                Session.builder()
                        .webSocketSession(session)
                        .objectMapper(outputObjectMapper)
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
