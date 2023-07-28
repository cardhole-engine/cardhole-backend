package com.github.cardhole.game.networking.cast.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RefreshCanBeCastAndActivatedListOutgoingMessage(

        List<UUID> canBeCastOrActivated
) implements Message {
}
