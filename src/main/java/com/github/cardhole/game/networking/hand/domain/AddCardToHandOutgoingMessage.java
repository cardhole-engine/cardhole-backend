package com.github.cardhole.game.networking.hand.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AddCardToHandOutgoingMessage(

        //TODO: Add more
        UUID id,
        String name,
        String set,
        int setId
) implements Message {
}
