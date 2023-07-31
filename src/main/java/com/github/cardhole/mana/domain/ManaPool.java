package com.github.cardhole.mana.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/*
 * 106.4. When an effect instructs a player to add mana, that mana goes into a player’s mana pool. From there, it can
 *    be used to pay costs immediately, or it can stay in the player’s mana pool as unspent mana. Each player’s mana
 *    pool empties at the end of each step and phase, and the player is said to lose this mana. Cards with abilities
 *    that produce mana or refer to unspent mana have received errata in the Oracle™ card reference to no longer
 *    explicitly refer to the mana pool.
 *       106.4a If any mana remains in a player’s mana pool after mana is spent to pay a cost, that player announces
 *           what mana is still there.
 *       106.4b If a player passes priority (see rule 117) while there is mana in their mana pool, that player
 *           announces what mana is there.
 */
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

    public void reset() {
        availableMana.clear();
    }
}
