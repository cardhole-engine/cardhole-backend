package com.github.cardhole.mana.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ManaPool {

    private Map<Mana, Integer> availableMana;

    public ManaPool() {
        this.availableMana = new EnumMap<>(Mana.class);
    }


    // TODO: It should have availableRedMana etc.

    public void addMana(final List<Mana> manaToAdd) {
        for (Mana mana : manaToAdd) {
            if (availableMana.containsKey(mana)) {
                availableMana.put(mana, availableMana.get(mana) + 1);
            } else {
                availableMana.put(mana, 1);
            }
        }
    }
}
