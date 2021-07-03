package com.darkgran.farstar.battle.gui;

import com.darkgran.farstar.battle.players.Player;
import com.darkgran.farstar.gui.PlayerBox2;

/**
 * Base for laying out menus.
 */
public abstract class BaseMenu extends PlayerBox2 implements Menu {
    private float offset;
    private boolean negativeOffset;

    public BaseMenu(float x, float y, boolean negativeOffset, BattleStage battleStage, Player player) {
        super(x, y, battleStage, player);
        this.negativeOffset = negativeOffset;
        setupOffset();
    }

    @Override
    public boolean isNegativeOffset() {
        return negativeOffset;
    }

    @Override
    public void setNegativeOffset(boolean negativeOffset) {
        this.negativeOffset = negativeOffset;
    }

    @Override
    public float getOffset() {
        return offset;
    }

    @Override
    public void setOffset(float offset) {
        this.offset = offset;
    }

}
