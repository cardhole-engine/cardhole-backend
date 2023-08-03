package com.github.cardhole.game.networking.battlefiled;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardTappedOnBattlefieldOutgoingMessage(

        UUID cardId
) implements Message {
}
