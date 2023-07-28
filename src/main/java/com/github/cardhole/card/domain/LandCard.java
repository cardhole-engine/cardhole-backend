package com.github.cardhole.card.domain;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.player.domain.Player;

public abstract class LandCard extends AbstractCard {

    public LandCard(final Game game, final Player owner, final String name, final Set set, final int setId) {
        super(game, owner, name, set, setId);
    }

    @Override
    public boolean canBeCast(final Game game) {
        return game.isStepActive(Step.PRECOMBAT_MAIN, Step.POSTCOMBAT_MAIN) && game.isStackEmpty()
                && !game.isWasLandCastedThisTurn();
    }

    @Override
    public void cast(final Player caster, final Target target) {
        game.getGameManager().castLandCardToPlayersBattlefield(caster, this);
    }
}
