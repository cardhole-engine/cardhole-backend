package com.github.cardhole.zone;

import com.github.cardhole.object.domain.GameObject;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

@Getter
public abstract class AbstractZone<T extends GameObject> implements Zone<T> {

    protected final LinkedList<T> objects = new LinkedList<>();

    public Optional<T> getGameObject(final UUID gameObjectId) {
        return objects.stream()
                .filter(card -> card.getId().equals(gameObjectId))
                .findFirst();
    }

    //TODO: Implement enter zone and leave zone here... They should clear the cards from any effects
    @Override
    public void enterZone(final T gameObject) {
        objects.add(gameObject);
    }

    @Override
    public void leaveZone(final T gameObject) {
        objects.remove(gameObject);
    }

    @Override
    public boolean isInZone(final T grameObject) {
        return objects.contains(grameObject);
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public int cardsInZone() {
        return objects.size();
    }
}
