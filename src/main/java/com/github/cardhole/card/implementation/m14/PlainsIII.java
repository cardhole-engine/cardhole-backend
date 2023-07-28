package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.LandCard;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

public class PlainsIII extends LandCard {

    public PlainsIII(final Game game, final Player owner) {
        super(game, owner, "Plains", Set.M14, 232);
    }
}
