package com.darkgran.farstar.battle.gui;

import com.darkgran.farstar.battle.players.Card;
import com.darkgran.farstar.battle.players.Fleet;
import com.darkgran.farstar.battle.players.Player;
import com.darkgran.farstar.util.SimpleBox2;

//uses Array and CardList remains null! (unlike TokenMenu)
public class FleetMenu extends BaseMenu implements DropTarget {
    private final SimpleBox2 simpleBox2 = new SimpleBox2();
    private final Fleet fleet;
    private FleetToken[] ships = new FleetToken[7];

    public FleetMenu(Fleet fleet, float x, float y, float width, float height, BattleStage battleStage, Player player, boolean negativeOffset) {
        super(x, y, negativeOffset, battleStage, player);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setupSimpleBox2(x, y, height, width);
        this.fleet = fleet;
        fleet.receiveFleetMenu(this);
        setupOffset();
    }

    @Override
    public void setupOffset() {
        super.setupOffset();
        if (isNegativeOffset()) { setOffset(getOffset()*-1); } //switching back, FleetMenu handles offset differently
    }

    public FleetToken addShip(Card card, int position) {
        float y = isNegativeOffset() ? getY() : getY()+getHeight()/2;
        ships[position] = new FleetToken(card, getX()+getOffset()*(position), y, getBattleStage(), null, this);
        return ships[position];
    }

    public void removeShip(int position) {
        ships[position] = null;
    }

    public Fleet getFleet() { return fleet; }

    public Token[] getShips() { return ships; }

    @Override
    public void setupSimpleBox2(float x, float y, float height, float width) {
        simpleBox2.setX(x);
        simpleBox2.setY(y);
        simpleBox2.setHeight(height);
        simpleBox2.setWidth(width);
    }

    @Override
    public SimpleBox2 getSimpleBox2() { return simpleBox2; }

}