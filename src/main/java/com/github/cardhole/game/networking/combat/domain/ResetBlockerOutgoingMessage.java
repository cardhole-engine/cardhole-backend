package com.github.cardhole.game.networking.combat.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record ResetBlockerOutgoingMessage(

) implements Message {
}
