package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;

public abstract class TapSourceAbility extends AbstractActivatedAbility {

    public TapSourceAbility(final PermanentCard source) {
        super(source);
    }

    @Override
    public void activate() {
        source.getGame().getGameManager().tapCardOnBattlefield(source);
    }

    @Override
    public boolean canBeActivated() {
        return source.isUntapped();
    }
}
