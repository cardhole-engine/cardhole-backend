package com.github.cardhole.game.networking.combat.domain;

import com.github.cardhole.networking.domain.Message;

import java.util.UUID;

public record BlockCreatureIncomingMessage(

        String type,
        UUID blockWith,
        UUID blockWhat
) implements Message {
}
