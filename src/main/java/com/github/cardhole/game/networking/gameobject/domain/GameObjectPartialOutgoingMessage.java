package com.github.cardhole.game.networking.gameobject.domain;

import com.github.cardhole.game.networking.battlefiled.GameObjectEnterToBattlefieldOutgoingMessage;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

//TODO: Have different types like token, emblem, spell, ability, etc...
@Builder
public record GameObjectPartialOutgoingMessage(

        UUID id,
        String name,
        UUID ownerId,
        String set,
        int setId,
        List<GameObjectEnterToBattlefieldOutgoingMessage.ActivatedActivity> activatedAbilities,
        int power,
        int toughness
) {
}
