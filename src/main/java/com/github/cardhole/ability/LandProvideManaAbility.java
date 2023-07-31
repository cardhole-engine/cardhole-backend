package com.github.cardhole.ability;

import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public class LandProvideManaAbility extends AbstractActivatedAbility {

    private final List<Mana> providedMana;

    public LandProvideManaAbility(final Mana... mana) {
        this.providedMana = List.of(mana);
    }

    @Override
    public void activate(final Player activator) {
        activator.getGame().getGameManager().addManaToPlayer(activator, providedMana);
    }

    @Override
    public boolean goesToStack() {
        return false;
    }
}
