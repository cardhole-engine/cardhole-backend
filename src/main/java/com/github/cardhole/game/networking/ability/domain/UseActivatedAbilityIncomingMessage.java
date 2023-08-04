package com.github.cardhole.game.networking.ability.domain;

import com.github.cardhole.networking.domain.Message;

import java.util.UUID;

public record UseActivatedAbilityIncomingMessage(

        String type,
        UUID abilityId
) implements Message {
}
