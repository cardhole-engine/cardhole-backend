package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.Set;
import com.github.cardhole.card.domain.land.PlainsLandCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

public class PlainsII extends PlainsLandCard {

    public PlainsII(final Game game, final Player owner) {
        super(game, owner, Set.M14, 231);
    }
}
