package com.darkgran.farstar.gui.battlegui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.darkgran.farstar.gui.ColorPalette;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.Battle;
import com.darkgran.farstar.gui.TextDrawer;
import com.darkgran.farstar.util.SimpleBox2;
import com.darkgran.farstar.util.SimpleVector2;

public class RoundCounter extends SimpleBox2 implements TextDrawer {
    private boolean wrap = false;
    private float wrapWidth = 0f;
    private final Texture pic = Farstar.ASSET_LIBRARY.get("images/rounds.png");
    private final Battle battle;
    private final BattleStage battleStage;
    private Color fontColor;
    private String fontPath = "fonts/bahnschrift30.fnt";
    private String text = "R#0";
    private SimpleVector2 textWH = TextDrawer.getTextWH(getFont(), text);

    public RoundCounter(float x, float y, BattleStage battleStage, Battle battle) {
        this.fontColor = ColorPalette.MAIN;
        setFont(fontPath);
        this.x = x;
        this.y = y;
        this.battle = battle;
        this.battleStage = battleStage;
    }

    public void update() {
        text = "R#"+battle.getRoundManager().getRoundNum();
        textWH = TextDrawer.getTextWH(getFont(), text);
    }

    public void draw(Batch batch) {
        batch.draw(pic, x, y);
        drawText(batch);
    }

    @Override
    public void drawText(Batch batch) {
        drawText(getFont(), batch, (x+pic.getWidth()*0.5f) - textWH.x*0.5f, (y+pic.getHeight()*0.5f) + textWH.y*0.5f, text, getFontColor());
    }

    @Override
    public String getFontPath() { return fontPath; }

    @Override
    public void setFontPath(String path) { this.fontPath = path; }

    @Override
    public Color getFontColor() { return fontColor; }

    @Override
    public void setFontColor(Color fontColor) { this.fontColor = fontColor; }

    @Override
    public boolean getWrap() {
        return wrap;
    }

    @Override
    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    @Override
    public float getWrapWidth() {
        return wrapWidth;
    }

    @Override
    public void setWrapWidth(float width) {
        wrapWidth = width;
    }

}
