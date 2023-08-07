package com.github.cardhole.game.networking.combat.domain;

import com.github.cardhole.networking.domain.Message;

import java.util.UUID;

public record DeclareAttackerIncomingMessage(

        String type,
        UUID cardId
) implements Message {
}
