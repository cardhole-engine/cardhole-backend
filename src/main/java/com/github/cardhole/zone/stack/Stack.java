package com.github.cardhole.zone.stack;

import com.github.cardhole.object.domain.GameObject;
import com.github.cardhole.zone.AbstractZone;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Stack extends AbstractZone<GameObject> {

    private boolean opponentPassedPriority;

    @Override
    public void enterZone(final GameObject gameObject) {
        super.enterZone(gameObject);

        opponentPassedPriority = false;
    }

    public Optional<GameObject> removeActiveEntry() {
        return Optional.ofNullable(objects.removeLast());
    }
}
