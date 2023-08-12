package com.github.cardhole.game.networking.gameobject;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.ability.HasActivatedAbilityAspect;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.game.networking.battlefiled.GameObjectEnterToBattlefieldOutgoingMessage;
import com.github.cardhole.game.networking.gameobject.domain.GameObjectPartialOutgoingMessage;
import com.github.cardhole.object.domain.GameObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameObjectPartialOutgoingMessageFactory {

    public GameObjectPartialOutgoingMessage newPartialMessage(final GameObject gameObject) {
        if (gameObject instanceof Card card) {
            final Optional<CreatureAspect> creatureAspectOptional = card.hasAspect(CreatureAspect.class)
                    ? Optional.of(card.getAspect(CreatureAspect.class)) : Optional.empty();

            return GameObjectPartialOutgoingMessage.builder()
                    .id(card.getId())
                    .name(card.getName())
                    .ownerId(card.getController().getId())
                    .set(card.getSet().name())
                    .setId(card.getSetId())
                    .activatedAbilities(card.getAspects(HasActivatedAbilityAspect.class).stream()
                            .map(HasActivatedAbilityAspect::getActivatedAbility)
                            .map(ability -> GameObjectEnterToBattlefieldOutgoingMessage.ActivatedActivity.builder()
                                    .id(ability.getId())
                                    .build()
                            )
                            .toList()
                    )
                    .power(
                            creatureAspectOptional
                                    .map(CreatureAspect::getPower)
                                    .orElse(-1)
                    )
                    .toughness(
                            creatureAspectOptional
                                    .map(CreatureAspect::getToughness)
                                    .orElse(-1)
                    )
                    .build();
        } else {
            throw new IllegalStateException("Unknown game object type!");
        }
    }
}
