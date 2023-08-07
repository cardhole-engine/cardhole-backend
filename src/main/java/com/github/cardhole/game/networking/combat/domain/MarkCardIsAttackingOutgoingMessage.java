package com.github.cardhole.game.networking.combat.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MarkCardIsAttackingOutgoingMessage(

        UUID cardId
) implements Message {
}
