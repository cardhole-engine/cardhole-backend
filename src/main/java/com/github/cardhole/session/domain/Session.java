package com.github.cardhole.session.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.player.domain.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Setter
@Builder
public class Session {

    private final ObjectMapper objectMapper;
    private final WebSocketSession webSocketSession;

    @Getter
    private String name;
    private UUID activeGameId;

    public void sendMessage(final Message message) {
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            //TODO: Custom exception
            throw new RuntimeException(e);
        }
    }

    public boolean isInGame() {
        return activeGameId != null;
    }

    public Optional<UUID> getActiveGameId() {
        return Optional.ofNullable(activeGameId);
    }
}
