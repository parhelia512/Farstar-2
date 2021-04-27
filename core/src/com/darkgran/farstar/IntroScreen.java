package com.darkgran.farstar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.darkgran.farstar.gui.NotificationManager;
import com.darkgran.farstar.gui.TableStage;
import com.darkgran.farstar.mainscreen.MainScreen;
import com.darkgran.farstar.util.Delayer;

public class IntroScreen extends SuperScreen implements Delayer { //Animation used only once on app-launch
    private final Texture logo = Farstar.ASSET_LIBRARY.getAssetManager().get("images/logo.jpg");
    private boolean active = false;
    private float alpha = 0;
    private boolean fadeDirection = true; //true in, false out

    private final static float INTRO_SPEED = 0.35f;

    public IntroScreen(final Farstar game, NotificationManager notificationManager) {
        super(game, notificationManager);
        Gdx.input.setCursorCatched(true);
        delayAction(this::activate, 0.5f);
    }

    @Override
    public void userEscape() {
        endIntro();
    }

    private void endIntro() {
        getGame().setScreen(new MainScreen(getGame(), new TableStage(getGame(), getViewport()), getNotificationManager()));
    }

    private void activate() { active = true; }

    private void updateAlpha(float delta) {
        //if (delta > 0.03f) { delta = 0.03f; }
        alpha += fadeDirection ? (INTRO_SPEED *delta) : -(INTRO_SPEED *delta)*4;
        if (alpha >= 1) {
            fadeDirection = false;
            active = false;
            delayAction(this::activate, 0.9f);
        }
    }

    @Override
    public void render(float delta) {
        //control
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            endIntro();
        }

        //INTRO ANIMATION

        if (active) {
            if (alpha < 0 && !fadeDirection) { //animation over
                active = false;
                fadeDirection = true;
                delayAction(this::endIntro, 0.5f);
            } else {
                updateAlpha(delta);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getCamera().update();
        getGame().batch.setProjectionMatrix(getCamera().combined);
        getGame().batch.begin();
        getGame().batch.setColor(1, 1, 1, (active || !fadeDirection) ? alpha : 0);

        getGame().batch.draw(logo, (float) (Farstar.STAGE_WIDTH / 2 - logo.getWidth() / 2), (float) (Farstar.STAGE_HEIGHT / 2 - logo.getHeight() / 2));

        drawSigns(getGame().batch);
        getGame().batch.end();

        update(delta);

    }

}