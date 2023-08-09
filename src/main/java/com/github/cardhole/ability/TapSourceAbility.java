package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;

public abstract class TapSourceAbility extends AbstractActivatedAbility {

    public TapSourceAbility(final Card source) {
        super(source);
    }

    @Override
    public void activate() {
        source.getGame().getGameManager().tapCardOnBattlefield(source);
    }

    @Override
    public boolean canBeActivated() {
        return source.hasAspect(PermanentAspect.class) && source.getAspect(PermanentAspect.class).isUntapped();
    }
}
