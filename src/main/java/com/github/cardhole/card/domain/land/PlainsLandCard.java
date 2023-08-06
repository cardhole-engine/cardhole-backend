package com.github.cardhole.card.domain.land;

import com.github.cardhole.ability.LandProvideManaAbility;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;

import java.util.Set;

public class PlainsLandCard extends LandCard {

    public PlainsLandCard(final Game game, final Player owner, final CardSet set, final int setId) {
        super(game, owner, "Plains", set, setId, Set.of(Supertype.BASIC), Set.of(Type.LAND),
                Set.of(Subtype.PLAINS));

        addActivatedAbility(new LandProvideManaAbility(this, Mana.WHITE));
    }
}
