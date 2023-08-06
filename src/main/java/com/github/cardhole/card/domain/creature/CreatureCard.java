package com.github.cardhole.card.domain.creature;

import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class CreatureCard extends PermanentCard {

    protected int attack;
    protected int defense;

    public CreatureCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                        final CardType cardType, final ManaCost manaCost, final int attack, final int defense) {
        super(game, owner, name, set, setId, cardType, manaCost);

        this.attack = attack;
        this.defense = defense;
    }

    @Override
    public void cast(final Target target) {
    }
}
