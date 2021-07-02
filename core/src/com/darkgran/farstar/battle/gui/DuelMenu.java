package com.darkgran.farstar.battle.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.DuelManager;
import com.darkgran.farstar.battle.players.LocalPlayer;

public abstract class DuelMenu {
    private final Texture duelCancel = Farstar.ASSET_LIBRARY.getAssetManager().get("images/duel_cancel.png");
    private final ImageButton cancelButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(duelCancel)));
    private final DuelManager duelManager;
    private BattleStage battleStage;  //must be set after ini - before RM.launch (see BattleScreen constructor)

    public DuelMenu(DuelManager duelManager) {
        this.duelManager = duelManager;
        duelManager.receiveDuelMenu(this);
    }

    public void setBattleStage(BattleStage battleStage) { this.battleStage = battleStage; }

    public void removeAllOKs() { }

    public void addOK(DuelOK duelOK) {
        if (duelOK.getDuelPlayer().getPlayer() instanceof LocalPlayer) {
            getBattleStage().addActor(duelOK);
            duelOK.setDisabled(false);
        }
    }

    public void removeOK(DuelOK duelOK) {
        duelOK.remove();
        duelOK.setDisabled(true);
    }

    public void addCancel() {
        getBattleStage().addActor(cancelButton);
        cancelButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                getDuelManager().cancelDuel();
            }
        });
    }

    public void removeCancel() {
        cancelButton.remove();
        cancelButton.removeListener(cancelButton.getClickListener());
    }

    public DuelManager getDuelManager() { return duelManager; }

    public BattleStage getBattleStage() { return battleStage; }

    public ImageButton getCancelButton() { return cancelButton; }



}
