package com.github.cardhole.zone.battlefield.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.zone.AbstractZone;
import lombok.Getter;

/*
 * 400.2. Public zones are zones in which all players can see the cards’ faces, except for those cards that some rule
 *     or effect specifically allow to be face down. Graveyard, battlefield, stack, exile, ante, and command are public
 *     zones. Hidden zones are zones in which not all players can be expected to see the cards’ faces. Library and hand
 *     are hidden zones, even if all the cards in one such zone happen to be revealed.
 * 400.4. Cards with certain card types can’t enter certain zones.
 *     400.4a If an instant or sorcery card would enter the battlefield, it remains in its previous zone.
 * 400.7. An object that moves from one zone to another becomes a new object with no memory of, or relation to, its
 *     previous existence. This rule has the following exceptions.
 *     400.7b Effects from static abilities that grant an ability to a permanent spell that functions on the battlefield
 *         continue to apply to the permanent that spell becomes (see rule 611.3d).
 *     400.7f Abilities of Auras that trigger when the enchanted permanent leaves the battlefield can find the new
 *         object that Aura became in its owner’s graveyard if it was put into that graveyard at the same time the
 *         enchanted permanent left the battlefield. It can also find the new object that Aura became in its owner’s
 *         graveyard as a result of being put there as a state-based action for not being attached to a permanent.
 *         (See rule 704.5m.)
 *     400.7i If an effect allows a land card to be played, other parts of that effect can find the new object that
 *         land card becomes after it moves to the battlefield as a result of being played this way.
 */
@Getter
public class Battlefield extends AbstractZone<Card> {

    @Override
    public void enterZone(final Card card) {
        super.enterZone(card);

        /*
         * 110.1. A permanent is a card or token on the battlefield. A permanent remains on the battlefield
         * indefinitely. A card or token becomes a permanent as it enters the battlefield and it stops being a permanent
         *  as it’s moved to another zone by an effect or rule.
         */
        card.addAspect(
                PermanentAspect.builder()
                        .assignedTo(card)
                        .build()
        );
    }

    @Override
    public void leaveZone(final Card card) {
        super.leaveZone(card);

        /*
         * 110.1. A permanent is a card or token on the battlefield. A permanent remains on the battlefield
         * indefinitely. A card or token becomes a permanent as it enters the battlefield and it stops being a permanent
         *  as it’s moved to another zone by an effect or rule.
         */
        card.removeAspect(PermanentAspect.class);
    }
}
