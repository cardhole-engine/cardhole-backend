package com.github.cardhole.game.networking.stop.domain;

import com.github.cardhole.game.domain.Step;
import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.Map;

@Builder
public record RefreshStopsOutgoingMessage(

        Map<Step, Boolean> stopAtStepInMyTurn,
        Map<Step, Boolean> stopAtStepInOpponentTurn
) implements Message {
}
