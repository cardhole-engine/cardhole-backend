package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.land.LandCard;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public class PlainsI extends LandCard {

    public PlainsI(final Game game, final Player owner) {
        super(game, owner, "Plains", Set.M14, 230, List.of());
    }
}
