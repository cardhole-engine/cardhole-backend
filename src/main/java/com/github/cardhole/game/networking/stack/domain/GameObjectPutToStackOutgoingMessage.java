package com.github.cardhole.game.networking.stack.domain;

import com.github.cardhole.game.networking.gameobject.domain.GameObjectPartialOutgoingMessage;
import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GameObjectPutToStackOutgoingMessage(

        GameObjectPartialOutgoingMessage gameObject
) implements Message {
}
