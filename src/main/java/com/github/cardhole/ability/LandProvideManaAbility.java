package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.mana.domain.Mana;

import java.util.List;

public class LandProvideManaAbility extends TapSourceAbility {

    private final List<Mana> providedMana;

    public LandProvideManaAbility(final Card source, final Mana... mana) {
        super(source);

        this.providedMana = List.of(mana);
    }

    @Override
    public void activate() {
        super.activate();

        source.getGame().getGameManager().addManaToPlayer(source.getController(), providedMana);
    }

    @Override
    public boolean goesToStack() {
        return false;
    }
}
