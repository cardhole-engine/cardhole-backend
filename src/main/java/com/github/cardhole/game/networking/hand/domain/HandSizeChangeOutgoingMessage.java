package com.github.cardhole.game.networking.hand.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record HandSizeChangeOutgoingMessage(

        UUID playerId,
        int handSize
) implements Message {
}
