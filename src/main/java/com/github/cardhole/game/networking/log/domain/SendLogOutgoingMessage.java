package com.github.cardhole.game.networking.log.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record SendLogOutgoingMessage(

        String message
) implements Message {
}
