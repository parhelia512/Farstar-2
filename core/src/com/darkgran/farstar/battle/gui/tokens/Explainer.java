package com.darkgran.farstar.battle.gui.tokens;

import com.badlogic.gdx.graphics.Color;
import com.darkgran.farstar.ColorPalette;
import com.darkgran.farstar.battle.players.cards.Card;
import com.darkgran.farstar.gui.TextDrawer;
import com.darkgran.farstar.gui.TextInTheBox;

public class Explainer extends TextInTheBox {

    public Explainer(Color fontColor, Color boxColor, String fontPath, String text, boolean noBox) {
        super(fontColor, boxColor, fontPath, text, noBox);
    }

    public Explainer() {
        super(ColorPalette.LIGHT, ColorPalette.changeAlpha(ColorPalette.DARK, 0.5f), "fonts/bahnschrift30.fnt", "", false);
        setWrapWidth(200f);
        setWrap(true);
    }

    public void setShiftedPosition(float x, float y) {
        setX(x+TokenType.PRINT.getWidth()*1.1f);
        setY(y+TokenType.PRINT.getHeight()*0.95f);
    }

    private void setupBox() {
        setupBox(getX(), getY(), getWrapWidth()+40f, TextDrawer.getTextWH(getFont(), getText(), getWrapWidth(), getWrap()).getY()+40f);
        centralizeBox();
        //getSimpleBox().setY(getSimpleBox().getY()-1f);
    }

    public void refreshText(Card card) {
        setText(getExplanation(card));
        setupBox();
    }

    protected String getExplanation(Card card) {
        return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    }

}
