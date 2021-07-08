package com.darkgran.farstar.battle.gui.tokens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.darkgran.farstar.battle.gui.BattleStage;
import com.darkgran.farstar.battle.gui.CardListMenu;
import com.darkgran.farstar.battle.players.cards.Card;

public abstract class ClickToken extends Token {
    private ClickListener clickListener = new ClickListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            click(button);
        }
    };

    public ClickToken(Card card, float x, float y, BattleStage battleStage, CardListMenu cardListMenu, TokenType tokenType, boolean noPics) {
        super(card, x, y, battleStage, cardListMenu, tokenType, noPics);
        this.addListener(clickListener);
        getTokenPrice().setClickListener(clickListener);
    }

    public void click(int button) { }

    public ClickListener getClickListener() {
        return clickListener;
    }

}
