package com.github.cardhole.game.networking.join.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record PlayerJoinedOutgoingMessage(

        String name
) implements Message {
}
