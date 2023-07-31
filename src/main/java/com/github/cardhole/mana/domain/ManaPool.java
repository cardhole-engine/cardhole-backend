package com.github.cardhole.mana.domain;

import java.util.EnumMap;
import java.util.Map;

public class ManaPool {

    private Map<Mana, Integer> availableMana;

    public ManaPool() {
        this.availableMana = new EnumMap<>(Mana.class);
    }


    // TODO: It should have availableRedMana etc.
}
