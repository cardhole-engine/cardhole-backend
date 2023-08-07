package com.github.cardhole.card.domain.land;

import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.permanent.Permanent;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

public abstract class LandCard extends PermanentCard implements Permanent {

    public LandCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                    final CardType cardType) {
        super(game, owner, name, set, setId, cardType, ManaCost.NO_COST);
    }

    @Override
    public boolean canBeCast() {
        return super.canBeCast() && !owner.getGame().isLandCastedThisTurn();
    }

    @Override
    public void cast(final Target target) {
        game.getGameManager().putLandCardToPlayersBattlefield(this);
    }
}
