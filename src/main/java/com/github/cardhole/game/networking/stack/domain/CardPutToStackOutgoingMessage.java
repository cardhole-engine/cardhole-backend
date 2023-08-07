package com.github.cardhole.game.networking.stack.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardPutToStackOutgoingMessage(

        //TODO: Other card data should be added!

        UUID id,
        String name,
        UUID ownerId,
        String set,
        int setId
) implements Message {
}
