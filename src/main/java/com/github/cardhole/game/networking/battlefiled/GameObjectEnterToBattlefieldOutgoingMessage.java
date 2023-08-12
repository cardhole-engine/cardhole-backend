package com.github.cardhole.game.networking.battlefiled;

import com.github.cardhole.game.networking.gameobject.domain.GameObjectPartialOutgoingMessage;
import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GameObjectEnterToBattlefieldOutgoingMessage(

        GameObjectPartialOutgoingMessage gameObject
) implements Message {

    @Builder
    public record ActivatedActivity(
            UUID id
    ) {
    }
}
