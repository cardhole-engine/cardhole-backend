package com.github.cardhole.card.domain;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;

public abstract class LandCard extends AbstractCard {

    public LandCard(final String name, final Set set, final int setId) {
        super(name, set, setId);
    }

    @Override
    public boolean canBeCast(final Game game) {
        return game.isStepActive(Step.PRECOMBAT_MAIN, Step.POSTCOMBAT_MAIN) && !game.isWasLandCastedThisTurn();
    }
}
