package com.github.cardhole.game.networking.join.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;

@Builder
public record JoinGameOutgoingMessage(

        String name,
        List<Player> players
) implements Message {

    @Builder
    public record Player(
            String name
    ) {
    }
}
