package com.github.cardhole.card.domain.land;

import com.github.cardhole.ability.LandProvideManaAbility;
import com.github.cardhole.card.domain.AbstractCard;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.aspect.ability.HasActivatedAbilityAspect;
import com.github.cardhole.card.domain.aspect.land.LandAspect;
import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;

public class PlainsLandCard extends AbstractCard {

    public PlainsLandCard(final Game game, final Player owner, final CardSet set, final int setId) {
        super(game, owner, "Plains", set, setId,
                CardType.builder()
                        .supertype(Supertype.BASIC)
                        .type(Type.LAND)
                        .subtype(Subtype.PLAINS)
                        .build(),
                ManaCost.NO_COST
        );

        addAspect(
                HasActivatedAbilityAspect.builder()
                        .activatedAbility(new LandProvideManaAbility(this, Mana.WHITE))
                        .assignedTo(this)
                        .build(),
                LandAspect.builder()
                        .assignedTo(this)
                        .build()
        );
    }
}
