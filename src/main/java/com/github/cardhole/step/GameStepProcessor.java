package com.github.cardhole.step;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;

public interface GameStepProcessor {

    void processStep(final Game game);

    Step forStep();
}
