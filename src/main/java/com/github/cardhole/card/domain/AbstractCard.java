package com.github.cardhole.card.domain;

import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.player.domain.Player;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public abstract class AbstractCard implements Card {

    protected final UUID id;
    protected final Game game;
    protected final String name;

    protected final CardSet set;
    protected final int setId;

    protected final CardType cardType;

    protected final ManaCost manaCost;

    /*
     * The player who (for purposes of the game) a card, permanent, token, or spell belongs to. See rules 108.3, 110.2,
     * 111.2, and 112.2.
     *
     * 108.3. The owner of a card in the game is the player who started the game with it in their deck. If a card is
     * brought into the game from outside the game rather than starting in a player’s deck, its owner is the player who
     * brought it into the game. If a card starts the game in the command zone, its owner is the player who put it into
     * the command zone to start the game. Legal ownership of a card in the game is irrelevant to the game rules except
     * for the rules for ante. (See rule 407.)
     */
    protected final Player owner;

    /*
     * Words that refer to an object’s controller, its would-be controller (if a player is attempting to cast or
     * activate it), or its owner (if it has no controller). See rule 109.5.
     *
     * 109.5. The words “you” and “your” on an object refer to the object’s controller, its would-be controller
     * (if a player is attempting to play, cast, or activate it), or its owner (if it has no controller). For a static
     * ability, this is the current controller of the object it’s on. For an activated ability, this is the player who
     * activated the ability. For a triggered ability, this is the controller of the object when the ability triggered,
     * unless it’s a delayed triggered ability. To determine the controller of a delayed triggered ability, see rules
     * 603.7d–f.
     */
    protected final Player controller;

    /*
     * The informal term "instant speed" means "whenever you would be able to cast instant spells". This is most often
     * used by the keyword Flash, granting "instancy" to any card type. Some abilities have "sorcery speed" baked into
     * their rules, and some cards allow them to be activated "any time you could cast an instant". However, the timing
     * restriction has been used to turn mana abilities into non-mana abilities, most famously Lion's Eye Diamond - this
     *  is to prevent it from casting spells, as part of the process of casting a spell is putting the spell from the
     * hand onto the stack before paying costs. Starting with Strixhaven: School of Mages, the card text "only any time
     * you could cast an instant" was shortened to "activate as an instant."
     *
     * @see https://mtg.fandom.com/wiki/Instant
     */
    protected boolean castWithInstantSpeed;

    public AbstractCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                        final CardType cardType, final ManaCost manaCost) {
        this.id = UUID.randomUUID();
        this.game = game;

        this.owner = owner;
        this.controller = owner;

        this.name = name;

        this.set = set;
        this.setId = setId;

        this.cardType = cardType;

        this.manaCost = manaCost;
    }

    @Override
    public boolean canBeCast() {
        if (!controller.getManaPool().hasManaAvailable(manaCost)) {
            return false;
        }

        /*
         * 505.6a The main phase is the only phase in which a player can normally cast artifact, creature, enchantment,
         *     planeswalker, and sorcery spells. The active player may cast these spells.
         */
        return castWithInstantSpeed || game.isActivePlayer(owner) && game.isStepActive(Step.PRECOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                && game.isStackEmpty();
    }

    @Override
    public Set<Supertype> getSupertype() {
        return cardType.getSupertype();
    }

    @Override
    public Set<Type> getType() {
        return cardType.getType();
    }

    @Override
    public Set<Subtype> getSubtype() {
        return cardType.getSubtype();
    }
}
