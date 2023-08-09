package com.github.cardhole.stack.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.object.domain.GameObject;
import com.github.cardhole.zone.AbstractZone;

import java.util.LinkedList;
import java.util.Optional;

public class Stack extends AbstractZone<GameObject> {

    private final LinkedList<StackEntry> stackEntries = new LinkedList<>();

    public void addCardToStack(final Card card) {
        stackEntries.add(
                StackEntry.builder()
                        .card(card)
                        .build()
        );
    }

    public boolean isEmpty() {
        return stackEntries.isEmpty();
    }

    public Optional<StackEntry> getActiveEntry() {
        return Optional.ofNullable(stackEntries.getLast());
    }

    public Optional<StackEntry> removeActiveEntry() {
        return Optional.ofNullable(stackEntries.removeLast());
    }
}
