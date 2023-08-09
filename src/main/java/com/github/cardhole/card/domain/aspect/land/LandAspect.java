package com.github.cardhole.card.domain.aspect.land;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.aspect.AbstractAspect;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LandAspect extends AbstractAspect {

    public LandAspect(final Card assignedTo) {
        super(assignedTo);
    }

    @Override
    public boolean canBeCast() {
        return !assignedTo.getGame().isLandCastedThisTurn();
    }

    @Override
    public void cast(final Target target) {
        assignedTo.getGame().getGameManager().putLandCardToPlayersBattlefield(assignedTo);
    }

    @Override
    public boolean isAttachableTo(final Card card) {
        return true;
    }
}
