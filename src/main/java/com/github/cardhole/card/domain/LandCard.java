package com.github.cardhole.card.domain;

import com.github.cardhole.game.domain.Game;

public abstract class LandCard extends AbstractCard {

    public LandCard(String name, Set set, int setId) {
        super(name, set, setId);
    }

    @Override
    public boolean canBeCast(final Game game) {
        return true; //TODO: Only in precombat and postcombat main if no land was cast already
    }
}
