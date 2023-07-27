package com.github.cardhole.game.networking.hand.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RemoveCardFromHandOutgoingMessage(

        UUID id
) implements Message {
}
