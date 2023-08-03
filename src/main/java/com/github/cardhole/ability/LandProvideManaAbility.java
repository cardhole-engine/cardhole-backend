package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public class LandProvideManaAbility extends TapSourceAbility {

    private final List<Mana> providedMana;

    public LandProvideManaAbility(final PermanentCard source, final Mana... mana) {
        super(source);

        this.providedMana = List.of(mana);
    }

    @Override
    public void activate(final Player activator) {
        super.activate(activator);

        activator.getGame().getGameManager().addManaToPlayer(activator, providedMana);
    }

    @Override
    public boolean goesToStack() {
        return false;
    }
}
