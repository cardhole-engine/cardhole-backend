package com.github.cardhole.game.networking.deck.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DeckSizeChangeOutgoingMessage(

        UUID playerId,
        int deckSize
) implements Message {
}
