package com.github.cardhole.game.networking.create.domain;

import com.github.cardhole.networking.domain.Message;

public record CreateGameIncomingMessage(

        String type,
        String name
) implements Message {
}
