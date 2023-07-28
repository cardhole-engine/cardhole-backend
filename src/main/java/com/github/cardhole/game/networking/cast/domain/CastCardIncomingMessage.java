package com.github.cardhole.game.networking.cast.domain;

import com.github.cardhole.card.domain.TargetType;
import com.github.cardhole.networking.domain.Message;

import java.util.UUID;

public record CastCardIncomingMessage(

        UUID cardId,
        UUID target,
        TargetType targetType
)  implements Message {
}
