package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.player.domain.Player;

public abstract class TapSourceAbility extends AbstractActivatedAbility {

    public TapSourceAbility(final Card source) {
        super(source);
    }

    @Override
    public void activate(final Player activator) {
        activator.getGame().getGameManager().tapCardOnBattlefield(source);
//TODO: Tap the source
    }
}
