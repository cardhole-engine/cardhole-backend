package com.github.cardhole.game.networking.start.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record DecideStartOrYieldOutgoingMessage(

        boolean shouldIStart
) implements Message {
}
