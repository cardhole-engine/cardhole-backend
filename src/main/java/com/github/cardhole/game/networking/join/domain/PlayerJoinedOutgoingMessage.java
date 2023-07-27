package com.github.cardhole.game.networking.join.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PlayerJoinedOutgoingMessage(

        UUID id,
        String name,
        int deckSize,
        int life
) implements Message {
}
