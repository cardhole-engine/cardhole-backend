package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.player.domain.Player;

public abstract class TapSourceAbility extends AbstractActivatedAbility {

    public TapSourceAbility(final PermanentCard source) {
        super(source);
    }

    @Override
    public void activate(final Player activator) {
        activator.getGame().getGameManager().tapCardOnBattlefield(source);
    }
}
