package com.darkgran.farstar.battle.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.BattleScreen;
import com.darkgran.farstar.battle.players.Player;

public class GUI1v1 extends GUI {
    private final ResourceMeter resourceMeter1;
    private final ResourceMeter resourceMeter2;
    private final MothershipToken mothershipToken1;
    private final MothershipToken mothershipToken2;
    public final ImageButton yardButton1 = new ImageButton(new TextureRegionDrawable(new TextureRegion(getYardPic())));
    private final YardMenu yardMenu1;
    //public final ImageButton yardButton2 = new ImageButton(new TextureRegionDrawable(new TextureRegion(getYardPic())));
    //private final YardMenu yardMenu2;

    public GUI1v1(Farstar game, Viewport viewport, BattleScreen battleScreen, Player player1, Player player2) {
        super(game, viewport, battleScreen);
        //Resources
        resourceMeter1 = new ResourceMeter(player1, true, Farstar.STAGE_WIDTH, 0f);
        resourceMeter2 = new ResourceMeter(player2, false, Farstar.STAGE_WIDTH, Farstar.STAGE_HEIGHT);
        //Motherships
        mothershipToken1 = new MothershipToken(player1.getMs(), Farstar.STAGE_WIDTH/2, Farstar.STAGE_HEIGHT*1/4);
        mothershipToken2 = new MothershipToken(player2.getMs(), Farstar.STAGE_WIDTH/2, Farstar.STAGE_HEIGHT*3/4);
        //Buttons
        turnButton.setBounds(Farstar.STAGE_WIDTH*6/7, Farstar.STAGE_HEIGHT/2, (float) Farstar.STAGE_WIDTH/20,(float) Farstar.STAGE_HEIGHT/20);
        this.addActor(turnButton);
        //Shipyards
        yardMenu1 = new YardMenu(player1.getShipyard(), false, Farstar.STAGE_WIDTH/2, Farstar.STAGE_HEIGHT/2);
        yardButton1.setBounds(Farstar.STAGE_WIDTH*1/9, Farstar.STAGE_HEIGHT*1/5, (float) Farstar.STAGE_WIDTH/20,(float) Farstar.STAGE_HEIGHT/20);
        this.addActor(yardButton1);
        /*yardMenu2 = new YardMenu(player2.getShipyard(), false, Farstar.STAGE_WIDTH/2, Farstar.STAGE_HEIGHT/2);
        yardButton2.setBounds(Farstar.STAGE_WIDTH*1/9, Farstar.STAGE_HEIGHT*1/5, (float) Farstar.STAGE_WIDTH/20,(float) Farstar.STAGE_HEIGHT/20);
        this.addActor(yardButton2);*/
        setupListeners();
    }

    @Override
    public void drawGUI(float delta, Batch batch) {
        resourceMeter2.draw(batch);
        resourceMeter1.draw(batch);
        mothershipToken1.draw(batch);
        mothershipToken2.draw(batch);
    }

    @Override
    public void setupListeners() {
        yardButton1.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                yardMenu1.switchVisibility();
            }
        });
        /*yardButton2.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                yardMenu2.switchVisibility();
            }
        });*/
        super.setupListeners();
    }

    @Override
    public void draw() {
        super.draw();
        //TODO draw yardMenu
    }

    @Override
    public void dispose() {
        yardButton1.removeListener(yardButton1.getClickListener());
        //yardButton2.removeListener(yardButton2.getClickListener());
        super.dispose();
    }
}
