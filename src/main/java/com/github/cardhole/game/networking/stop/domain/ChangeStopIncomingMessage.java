package com.github.cardhole.game.networking.stop.domain;

import com.github.cardhole.game.domain.Step;
import com.github.cardhole.networking.domain.Message;

public record ChangeStopIncomingMessage(

        String type,
        Step step,
        boolean myTurn,
        boolean newValue
) implements Message {
}
