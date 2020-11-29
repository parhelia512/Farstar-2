package com.darkgran.farstar.battle.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.darkgran.farstar.battle.players.Card;
import com.darkgran.farstar.battle.players.Fleet;
import com.darkgran.farstar.util.SimpleBox2;

public class FleetMenu extends SimpleBox2 { //not a TokenMenu (Array vs ArrayList)
    private final BattleStage battleStage;
    private GlyphLayout layout = new GlyphLayout();
    private float offset;
    private final Fleet fleet;
    private Token[] ships = new Token[7];

    public FleetMenu(Fleet fleet, float x, float y, float width, float height, BattleStage battleStage) {
        this.battleStage = battleStage;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.fleet = fleet;
        fleet.receiveFleetMenu(this);
        setupOffset();
    }

    public void setupOffset() {
        String res = "Battlestation";
        layout.setText(new BitmapFont(), res);
        offset = layout.width;
    }

    public void addShip(Card card, int position) {
        ships[position] = new Token(card, getX(), getY(), battleStage, null);
    }

    public float getOffset() { return offset; }

    public void setOffset(float offset) { this.offset = offset; }

    public GlyphLayout getLayout() { return layout; }

    public Fleet getFleet() { return fleet; }

    public Token[] getShips() { return ships; }

}