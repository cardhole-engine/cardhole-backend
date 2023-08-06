package com.github.cardhole.card.implementation.m14;

import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.creature.CreatureCard;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.Set;

public class PillarfieldOx extends CreatureCard {


    public PillarfieldOx(final Game game, final Player owner) {
        super(game, owner, "Pillarfield Ox", CardSet.M14, 28, Set.of(Subtype.OX), 2, 4);
    }
}
