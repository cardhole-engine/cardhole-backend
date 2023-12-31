package com.github.cardhole.game.networking.battlefiled;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

//TODO: This message should be called CardTappedOutgoingMessage because cards can only be tapped on the battlefield
@Builder
public record CardTappedOnBattlefieldOutgoingMessage(

        UUID cardId
) implements Message {
}
