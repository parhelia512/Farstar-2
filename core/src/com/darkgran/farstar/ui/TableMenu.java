package com.darkgran.farstar.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.Farstar;


public class TableMenu extends ListeningMenu {
    private final ImageButton exitButton;
    private final Texture exit;

    public TableMenu(final Farstar game, Viewport viewport) {
        super(game, viewport);
        exit = new Texture("exit.png");
        exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exit)));
        exitButton.setBounds(1, 1, (float) Farstar.STAGE_WIDTH/40,(float) Farstar.STAGE_HEIGHT/20);
        this.addActor(exitButton);
        setupListeners();
    }

    @Override
    public void setupListeners() {
        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.exit(0);
            }
        });
    }
}
