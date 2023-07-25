package com.github.cardhole.game.networking.join.domain;

import com.github.cardhole.networking.domain.Message;

public record RequestJoinIncomingMessage(

        String type,
        String gameId
) implements Message {
}
