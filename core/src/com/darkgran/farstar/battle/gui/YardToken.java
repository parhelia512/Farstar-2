package com.darkgran.farstar.battle.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.darkgran.farstar.battle.players.Card;

public class YardToken extends Token {
    private final YardMenu yardMenu;

    public YardToken(Card card, float x, float y, BattleStage battleStage, YardMenu yardMenu) {
        super(card, x, y, battleStage, yardMenu);
        this.yardMenu = yardMenu;
    }

    @Override
    public void setupListener() {
        this.addListener(new InputListener()
        {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (yardMenu.isVisible()) {
                    getBattleStage().setFakeToken(new FakeToken(getCard(), getX(), getY(), getBattleStage(), getTokenMenu()));
                    event.setRelatedActor(getBattleStage().getFakeToken());
                    event.getStage().addTouchFocus(getBattleStage().getFakeToken().getDragger().getInputListener(), getBattleStage().getFakeToken(), getBattleStage().getFakeToken(), event.getPointer(), event.getButton());
                }
                return false;
            }
        });
    }



}
