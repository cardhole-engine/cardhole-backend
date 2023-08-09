package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.AbstractCard;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

public class PillarfieldOx extends AbstractCard {

    public PillarfieldOx(final Game game, final Player owner) {
        super(game, owner, "Pillarfield Ox", CardSet.M14, 28,
                CardType.builder()
                        .type(Type.CREATURE)
                        .subtype(Subtype.OX)
                        .build(),
                ManaCost.builder()
                        .white(1)
                        .colorless(3)
                        .build()
        );

        addAspect(
                PermanentAspect.builder()
                        .assignedTo(this)
                        .build(),
                CreatureAspect.builder()
                        .power(2)
                        .toughness(4)
                        .assignedTo(this)
                        .build()
        );
    }
}
