package com.darkgran.farstar.battle.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.ListeningStage;
import com.darkgran.farstar.battle.Battle;
import com.darkgran.farstar.battle.BattleScreen;
import com.darkgran.farstar.battle.players.Card;
import com.darkgran.farstar.battle.players.Fleet;
import com.darkgran.farstar.util.SimpleBox2;

import java.awt.*;

public abstract class BattleStage extends ListeningStage {
    private final BattleScreen battleScreen;
    private final Texture turn = new Texture("images/turn.png");
    public final ImageButton turnButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(turn)));
    private final Texture yardPic = new Texture("images/yard.png");
    private FakeToken fakeToken;

    public BattleStage(final Farstar game, Viewport viewport, BattleScreen battleScreen) {
        super(game, viewport);
        this.battleScreen = battleScreen;
    }

    public void processDrop(float x, float y, Card card, TokenMenu tokenMenu) {

    }

    public void drawBattleStage(float delta, Batch batch) {
        if (fakeToken != null) { fakeToken.draw(batch); }
    }

    @Override
    public void setupListeners() {
        turnButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
               battleScreen.getBattle().getRoundManager().endTurn();
            }
        });
    }

    @Override
    public void dispose() {
        turnButton.removeListener(turnButton.getClickListener());
        turn.dispose();
        super.dispose();
    }

    public void drawTokenMenu(TokenMenu tokenMenu, Batch batch) {
        for (int i = 0; i < tokenMenu.getTokens().size(); i++) {
            tokenMenu.getTokens().get(i).draw(batch);
        }
    }

    public void drawFleet(FleetMenu fleetMenu, Batch batch) {
        for (int i = 0; i < fleetMenu.getShips().length; i++) {
            if (fleetMenu.getShips()[i] != null) {
                fleetMenu.getShips()[i].draw(batch);
            }
        }
    }

    public boolean isInBox(SimpleBox2 simpleBox2, float x, float y) {
        Rectangle rectangle = new Rectangle((int) simpleBox2.getX(), (int) simpleBox2.getY(), (int) simpleBox2.getWidth(), (int) simpleBox2.getHeight());
        return rectangle.contains(x, y);
    }

    public Texture getYardPic() { return yardPic; }

    public BattleScreen getBattleScreen() { return battleScreen; }

    public FakeToken getFakeToken() { return fakeToken; }

    public void setFakeToken(FakeToken fakeToken) {
        if (this.fakeToken != null) { this.fakeToken.remove(); }
        this.fakeToken = fakeToken;
        if (fakeToken != null) { this.addActor(fakeToken); }
    }

}
