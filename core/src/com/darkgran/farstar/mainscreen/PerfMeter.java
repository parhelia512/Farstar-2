package com.darkgran.farstar.mainscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.darkgran.farstar.util.JustFont;
import com.darkgran.farstar.util.TextDrawer;

public class PerfMeter extends JustFont implements TextDrawer {

    public PerfMeter(float x, float y, Color fontColor) {
        super("fonts/barlow24.fnt");
        setX(x);
        setY(y);
        setFontColor(fontColor);
    }

    @Override
    public void draw(Batch batch) {
        String txt = "...";
        draw(getFont(), batch, getX(), getY(), txt, fontColor);
    }

}
