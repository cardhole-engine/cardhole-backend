package com.github.cardhole.game.networking.join.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record JoinGameOutgoingMessage(

        String name,
        List<Player> players
) implements Message {

    @Builder
    public record Player(
            UUID id,
            String name,
            int deckSize,
            int life,
            boolean myPlayer
    ) {
    }
}
