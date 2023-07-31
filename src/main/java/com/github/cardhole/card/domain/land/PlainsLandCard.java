package com.github.cardhole.card.domain.land;

import com.github.cardhole.ability.LandProvideManaAbility;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public class PlainsLandCard extends LandCard {

    public PlainsLandCard(final Game game, final Player owner, final Set set, final int setId) {
        super(game, owner, "Plains", set, setId, List.of(new LandProvideManaAbility(Mana.WHITE)));
    }
}
