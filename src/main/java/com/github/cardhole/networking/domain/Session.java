package com.github.cardhole.networking.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Setter
@Builder
public class Session {

    private final ObjectMapper objectMapper;
    private final WebSocketSession webSocketSession;

    private boolean inGame = false;

    public void sendMessage(final Message message) {
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            //TODO: Custom exception
            throw new RuntimeException(e);
        }
    }
}
