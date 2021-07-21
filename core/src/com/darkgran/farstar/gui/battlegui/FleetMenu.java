package com.darkgran.farstar.gui.battlegui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.darkgran.farstar.SuperScreen;
import com.darkgran.farstar.battle.players.BattlePlayer;
import com.darkgran.farstar.battle.players.BattleCard;
import com.darkgran.farstar.gui.tokens.FleetToken;
import com.darkgran.farstar.gui.tokens.TokenType;
import com.darkgran.farstar.battle.players.Fleet;
import com.darkgran.farstar.cards.CardInfo;
import com.darkgran.farstar.cards.CardType;
import com.darkgran.farstar.battle.players.Ship;
import com.darkgran.farstar.util.SimpleBox2;
import com.darkgran.farstar.util.SimpleVector2;

//The only "Menu" that does NOT extend CardListMenu! (ie. Fleet is not a CardList!)
//(uses Array instead of ArrayList)
public class FleetMenu extends BaseActorMenuBattle implements DropTarget {
    private final Fleet fleet;
    private FleetToken[] fleetTokens = new FleetToken[7]; //in-future: consider removing (FleetToken is held by the Ship itself)
    private FleetToken[] tokensPrediction = new FleetToken[7];
    private ClickListener clickListener = new ClickListener(){};
    private boolean predicting;
    private boolean predictEnabled;

    public FleetMenu(Fleet fleet, float x, float y, float width, float height, BattleStage battleStage, BattlePlayer battlePlayer, boolean negativeOffset) {
        super(x, y, negativeOffset, battleStage, battlePlayer);
        setWidth(width);
        setHeight(height);
        setupSimpleBox2(x, y, width, height);
        this.fleet = fleet;
        fleet.receiveFleetMenu(this);
        setupOffset();
        addListener(clickListener);
    }

    private float getBaseY() {
        return isNegativeOffset() ? getY() + getHeight() - TokenType.FLEET.getHeight() - 80f : getY() + 80f;
    }

    public FleetToken addShip(BattleCard battleCard, int position) {
        fleetTokens[position] = new FleetToken(battleCard, getX()+getOffset()*(position), getBaseY(), getBattleStage(), null, this, false, true);
        updateCoordinates(fleetTokens);
        return fleetTokens[position];
    }

    public void updateCoordinates(FleetToken[] arr) { //provides a visual shift of tokens so the even numbers seem centralized
        int count = 0;
        int left = 0;
        int right = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                count++;
                if (i < 3) {
                    left++;
                } else if (i > 3) {
                    right++;
                }
            }
        }
        if (count != 0) {
            if (count % 2 == 0) { //even
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != null) {
                        arr[i].setX(getX() + ((left > right) ? getOffset()/2 : -getOffset()/2) + getOffset() * i);
                    }
                }
            } else { //odd
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != null) {
                        arr[i].setX(getX() + getOffset() * i);
                    }
                }
            }
        }
    }



    private void predictCoordinates() {
        SimpleVector2 coords = SuperScreen.getMouseCoordinates();
        int pos = getBattleStage().getRoundDropPosition(coords.x, coords.y, this, CardType.YARDPRINT);
        if (fleet.hasSpace() && pos > -1 && pos < 7) { //fleet.addShip() validation
            //Copy ships
            Ship[] shipsPrediction = new Ship[7];
            for (int c = 0; c < fleet.getShips().length; c++) {
                if (fleet.getShips()[c] != null) {
                    shipsPrediction[c] = new Ship(fleet, fleet.getShips()[c].getCardInfo(), getBattlePlayer());
                    shipsPrediction[c].setToken(fleet.getShips()[c].getToken());
                } else {
                    shipsPrediction[c] = null;
                }
            }
            //Proceed with fleet.addShip()
            boolean side = pos < 3;
            int start = side ? 2 : 4;
            if (pos == 3) {
                SimpleVector2 lr = getFleet().getSideSizes(shipsPrediction);
                side = lr.x < lr.y;
                start = 3;
            }
            int end = side ? -1 : 7;
            int change = side ? -1 : 1;
            Ship shipToSet = new Ship(getFleet(), new CardInfo(), getBattlePlayer()); // = null;
            shipToSet.setToken(new FleetToken(null, getX(), getBaseY(), getBattleStage(), null, TokenType.FLEET, this, true, false));
            shipToSet.getToken().setTouchable(Touchable.disabled);
            int i;
            boolean sideHasSpace = false;
            for (i = start; i != end; i += change) {
                if (shipsPrediction[i] == null) {
                    sideHasSpace = true;
                    break;
                }
            }
            if (!sideHasSpace) {
                shiftAllPredictions(shipsPrediction, tokensPrediction, side);
            }
            for (i = start; i != end; i += change) {
                if (shipsPrediction[i] != null) {
                    if (i == pos) {
                        Ship holder = shipsPrediction[i];
                        shipsPrediction[i] = shipToSet;
                        shipToSet = holder;
                        pos += change;
                    }
                } else {
                    shipsPrediction[i] = shipToSet;
                    break;
                }
            }
            centralizePredictions(shipsPrediction, tokensPrediction);
            for (int c = 0; c < shipsPrediction.length; c++) {
                if (shipsPrediction[c] == null) {
                    tokensPrediction[c] = null;
                } else {
                    FleetToken token = (FleetToken) shipsPrediction[c].getToken();
                    tokensPrediction[c] = new FleetToken(token.getCard(), token.getX(), token.getY(), token.getBattleStage(), null, TokenType.FLEET, this, token.isNoPics(), false);
                    tokensPrediction[c].setTouchable(Touchable.disabled);
                }
            }
            updateCoordinates(tokensPrediction);
        }
    }

    private void shiftAllPredictions(Ship[] shipArr, FleetToken[] tokenArr, boolean fromSide) { //"Simulates" Fleet.shiftAllShips()
        int start = fromSide ? 6 : 0;
        int end = fromSide ? 0 : 6;
        int change = fromSide ? -1 : 1;
        for (int i = start; i != end; i+=change) {
            if (shipArr[i+change] != null) {
                shipArr[i] = shipArr[i+change];
                shipArr[i+change].setToken(shipArr[i+change].getToken());
                tokenArr[i] = (FleetToken) shipArr[i+change].getToken();
                shipArr[i+change] = null;
                tokenArr[i+change] = null;
            }
        }
    }

    private void centralizePredictions(Ship[] shipArr, FleetToken[] tokenArr) { //"Simulates" Fleet.centralizeShips()
        int left = 0;
        int right = 0;
        for (int i = 0; i < shipArr.length; i++) {
            if (shipArr[i] != null) {
                if (i < 3) {
                    left++;
                } else if (i > 3) {
                    right++;
                }
            }
        }
        if (Math.abs(left-right) > 1) {
            shiftAllPredictions(shipArr, tokenArr, left > right);
        }
    }

    public void drawTokens(Batch batch) {
        if (!fleet.isEmpty() && predictEnabled) {
            boolean overToken = false;
            SimpleVector2 coords = SuperScreen.getMouseCoordinates();
            Vector2 mousePosition = screenToLocalCoordinates(new Vector2(coords.x, coords.y));
            for (FleetToken fleetToken : fleetTokens) {
                if (fleetToken != null && fleetToken.getClickListener().isOver(fleetToken, mousePosition.x, mousePosition.y)) {
                    overToken = true;
                    break;
                }
            }
            predicting = clickListener.isOver(this, mousePosition.x, mousePosition.y) || overToken;
            if (predicting) {
                predictCoordinates();
            }
        } else {
            predicting = false;
        }
        if (!predicting) {
            drawShips(fleetTokens, batch);
        } else {
            drawShips(tokensPrediction, batch);
        }
    }

    private void drawShips(FleetToken[] arr, Batch batch) {
        for (FleetToken fleetToken : arr) {
            if (fleetToken != null) {
                fleetToken.draw(batch);
            }
        }
    }

    public static int getFleetTokenCount(FleetToken[] tokens) {
        int count = 0;
        for (FleetToken fleetToken : tokens) {
            if (fleetToken != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void setupOffset() {
        setOffset(TokenType.FLEET.getWidth()*1.03f);
    }

    @Override
    public boolean isEmpty() {
        for (FleetToken ship : fleetTokens) { if (ship != null) { return false; } }
        return true;
    }

    public void removeShip(int position, boolean noUpdate) {
        fleetTokens[position] = null;
        if (!noUpdate) { updateCoordinates(fleetTokens); }
    }

    public void overwriteToken(int position, FleetToken fleetToken) {
        fleetTokens[position] = fleetToken;
        updateCoordinates(fleetTokens);
    }

    public Fleet getFleet() { return fleet; }

    public FleetToken[] getFleetTokens() { return fleetTokens; }

    public boolean isPredicting() { return predicting; }

    public boolean isPredictEnabled() {
        return predictEnabled;
    }

    public void setPredictEnabled(boolean predictEnabled) {
        this.predictEnabled = predictEnabled;
    }

    @Override
    public SimpleBox2 getSimpleBox2() { return new SimpleBox2(getX(), getY(), getWidth(), getHeight()); }

}