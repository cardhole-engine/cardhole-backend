package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.land.PlainsLandCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

public class PlainsIII extends PlainsLandCard {

    public PlainsIII(final Game game, final Player owner) {
        super(game, owner, CardSet.M14, 232);
    }
}
