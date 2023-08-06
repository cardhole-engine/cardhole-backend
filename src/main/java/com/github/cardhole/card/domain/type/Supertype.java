package com.github.cardhole.card.domain.type;

/**
 * 205.4a An object can have one or more supertypes. A card’s supertypes are printed directly before its card types.
 *      The supertypes are basic, legendary, ongoing, snow, and world.
 * 205.4b An object’s supertype is independent of its card type and subtype, even though some supertypes are closely
 *     identified with specific card types. Changing an object’s card types or subtypes won’t change its supertypes.
 *     Changing an object’s supertypes won’t change its card types or subtypes. When an object gains or loses a
 *     supertype, it retains any other supertypes it had.
 *     Example: An ability reads, “All lands are 1/1 creatures that are still lands.” If any of the affected lands
 *         were legendary, they are still legendary.
 * 205.4c Any land with the supertype “basic” is a basic land. Any land that doesn’t have this supertype is a
 *     nonbasic land, even if it has a basic land type.
 *     Cards printed in sets prior to the Eighth Edition core set didn’t use the word “basic” to indicate a basic
 *     land. Cards from those sets with the following names are basic lands and have received errata in the Oracle
 *     card reference accordingly: Forest, Island, Mountain, Plains, Swamp, Snow-Covered Forest, Snow-Covered
 *     Island, Snow-Covered Mountain, Snow-Covered Plains, and Snow-Covered Swamp.
 * 205.4d Any permanent with the supertype “legendary” is subject to the state-based action for legendary permanents,
 *     also called the “legend rule” (see rule 704.5j).
 * 205.4e Any instant or sorcery spell with the supertype “legendary” is subject to a casting restriction. A player
 *     can’t cast a legendary instant or sorcery spell unless that player controls a legendary creature or a legendary
 *     planeswalker.
 * 205.4f Any permanent with the supertype “world” is subject to the state-based action for world permanents, also
 *     called the “world rule” (see rule 704.5k).
 * 205.4g Any permanent with the supertype “snow” is a snow permanent. Any permanent that doesn’t have this supertype
 *     is a nonsnow permanent, regardless of its name.
 * 205.4h Any scheme card with the supertype “ongoing” is exempt from the state-based action for schemes
 *     (see rule 704.6e).
 */
public enum Supertype {

    BASIC
}
