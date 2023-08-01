package com.github.cardhole.game.networking.mana.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RefreshManaPoolOutgoingMessage(

        UUID playerId,
        int whiteMana,
        int blueMana,
        int blackMana,
        int redMana,
        int greenMana,
        int colorlessMana

) implements Message {
}
