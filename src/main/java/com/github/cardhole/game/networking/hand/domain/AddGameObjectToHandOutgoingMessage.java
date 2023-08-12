package com.github.cardhole.game.networking.hand.domain;

import com.github.cardhole.game.networking.gameobject.domain.GameObjectPartialOutgoingMessage;
import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record AddGameObjectToHandOutgoingMessage(

        GameObjectPartialOutgoingMessage gameObject
) implements Message {
}
