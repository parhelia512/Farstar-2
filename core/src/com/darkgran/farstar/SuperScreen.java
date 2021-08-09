package com.darkgran.farstar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.gui.ColorPalette;
import com.darkgran.farstar.gui.NotificationManager;
import com.darkgran.farstar.gui.TableStage;
import com.darkgran.farstar.gui.YXQuestionBox;
import com.darkgran.farstar.gui.SimpleVector2;

import static com.darkgran.farstar.Farstar.ASSET_LIBRARY;

public abstract class SuperScreen implements Screen {
    public final static boolean DEBUG_RENDER = false;
    private final Farstar game;
    private final static OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new ExtendViewport(Farstar.STAGE_WIDTH, Farstar.STAGE_HEIGHT, camera);
    private final static ShapeRenderer shapeRenderer = new ShapeRenderer();
    private TableStage tableStage; //in-future: possibly make static (but needs to be disabled on Screens like Intro)
    private YXQuestionBox screenConceder = null;
    private final PerfMeter perfMeter = new PerfMeter((float) (Farstar.STAGE_WIDTH*0.0885), (float) (Farstar.STAGE_HEIGHT*0.98), ColorPalette.MAIN); //in-future: possibly make static (but needs to be disabled on Screens like Intro)
    public enum CursorType {
        DEFAULT, AIM
    }
    //For some reason, there is a drastic loss of image quality between pixmap (which are what the Gdx.graphics.setCursor accepts) and png.
    //In-future: Look into Gdx.graphics and either find how to use the system cursor while keeping the quality OR make low-quality cursors for "low quality graphic settings" and use it only then.
    /*public static void switchCursor(CursorType cursorType) {
        Gdx.graphics.setCursor(getCursor(cursorType));
    }
    public static Cursor getCursor(CursorType cursorType) {
        switch (cursorType) {
            default:
            case DEFAULT:
                return Gdx.graphics.newCursor(ASSET_LIBRARY.get("images/cursor_default.png"), 0, 0);
            case AIM:
                return Gdx.graphics.newCursor(ASSET_LIBRARY.get("images/cursor_aim.png"), 16, 16);
        }
    }*/
    private static TextureRegion hdCursor;
    private static int hdCursorOffsetX;
    private static int hdCursorOffsetY;
    private static boolean hdCursorInUse;
    public static void switchCursor(CursorType cursorType) {
        switch (cursorType) {
            default:
            case DEFAULT:
                hdCursor = ASSET_LIBRARY.getAtlasRegion("cursor-default");
                hdCursorOffsetX = 0;
                hdCursorOffsetY = 0;
                break;
            case AIM:
                hdCursor = ASSET_LIBRARY.getAtlasRegion("cursor-aim");
                hdCursorOffsetX = 16;
                hdCursorOffsetY = 16;
                break;
        }
    }
    private static void drawCursor(Batch batch, Farstar game) {
        boolean HDOn = false;
        if (hdCursor != null) {
            SimpleVector2 coords = getMouseCoordinates();
            if (game.MWQ.isMouseInsideWindow()) {
                HDOn = true;
                float scale = getScreenScale();
                batch.draw(hdCursor, coords.x - hdCursorOffsetX, Farstar.STAGE_HEIGHT - (coords.y - hdCursorOffsetY) - hdCursor.getRegionHeight(), hdCursor.getRegionWidth()/2f, hdCursor.getRegionHeight()/2f, hdCursor.getRegionWidth(), hdCursor.getRegionHeight(), scale, scale, 0);
            }
        }
        if (HDOn != hdCursorInUse) {
            hdCursorInUse = HDOn;
            hideCursor(HDOn);
        }
    }
    public static float getScreenScale() {
        float scaleX = (float) Farstar.STAGE_WIDTH / Gdx.graphics.getWidth();
        float scaleY = (float) Farstar.STAGE_HEIGHT / Gdx.graphics.getHeight();
        return Math.max(scaleX, scaleY);
    }
    public static void hideCursor(boolean hide) {
        if (hide) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
        } else { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
    }
    public static SimpleVector2 getMouseCoordinates() {
        Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(pos);
        return new SimpleVector2(pos.x, Farstar.STAGE_HEIGHT-pos.y);
    }
    public static final class ScreenSettings { //in-future: use Singleton-pattern instead of static + check thread-safety
        public static boolean tableStageEnabled = true;
        public static boolean netEnabled = false;
        public static boolean tokenFramesEnabled = true;
        public static boolean perfMeterEnabled = true;
        public static boolean f1buttonEnabled = true;
        private ScreenSettings() {}
    }
    public void setTableStageEnabled(boolean tableStageEnabled) {
        ScreenSettings.tableStageEnabled = tableStageEnabled;
        if (tableStage != null) { tableStage.enableButtons(tableStageEnabled); }
    }
    private ScreenSettings screenSettings;


    public SuperScreen(final Farstar game) {
        this.game = game;
        camera.setToOrtho(false, Farstar.STAGE_WIDTH, Farstar.STAGE_HEIGHT);
        viewport.apply();
        camera.position.set((float) Farstar.STAGE_WIDTH/2,(float) Farstar.STAGE_HEIGHT/2,0);
        switchCursor(CursorType.DEFAULT);
    }

    protected void setTableMenu(TableStage tableStage) {
        this.tableStage = tableStage;
        this.tableStage.setViewport(viewport);
        if (!game.getInputMultiplexer().getProcessors().contains(tableStage, true)) {
            game.getInputMultiplexer().addProcessor(tableStage);
        }
    }

    public void userEscape() { }

    protected void drawContent(float delta, Batch batch) { }//for all screens except intro

    protected void drawTopContent(float delta, Batch batch) { }//for all screens except intro

    protected void drawMenus(float delta, Batch batch) { //for all screens except intro
        batch.begin();
        if (ScreenSettings.perfMeterEnabled) { perfMeter.drawText(batch); }
        //All buttons, included the ones in YXQuestionBox, are drawn when the entire Stage is drawn, therefore moving screenConceder higher makes the box draw over the buttons.
        //To be able to move screenConceder to a "higher layer", another Stage would need to be set up ("TopStage"), however this is unnecessary unless more features would use this Stage.
        if (screenConceder != null) { screenConceder.draw(batch, shapeRenderer); }
        batch.end();
    }

    protected void drawSigns(Batch batch) {
        NotificationManager.drawAll(batch, shapeRenderer);
    }

    public static void switchFullscreen() {
        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
        } else {
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (tableStage != null) { //should persist over all screens
            tableStage.drawBackground(game.batch, ScreenSettings.tableStageEnabled, ScreenSettings.netEnabled);
            if (ScreenSettings.tableStageEnabled) {
                tableStage.act(delta);
                tableStage.draw();
            }
        }

        game.batch.begin(); //batch.begin() and batch.end() should be minimized (preferably only 1 of each per 1 frame... consider sharing the main batch with Stages)
        //other things to limit ("not to make batch flush"): changes to projection/transformation matrices (try Affine2 instead?), enabling/disabling blending (also: OpenGL calls vs spriteBatch.enableBlending())
        game.batch.setColor(1, 1, 1, 1);
        drawContent(delta, game.batch);
        game.batch.end();
        drawMenus(delta, game.batch);
        game.batch.begin();
        drawTopContent(delta, game.batch);
        drawSigns(game.batch);
        drawCursor(game.batch, game);
        game.batch.end();

        update(delta);

    }

    protected void update(float delta) {
        NotificationManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set((float) Farstar.STAGE_WIDTH/2,(float) Farstar.STAGE_HEIGHT/2,0);
        camera.update();
        Gdx.graphics.setVSync(Gdx.graphics.isFullscreen()&&game.currentFPSCap!=0);
    }

    @Override
    public void show() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { hideScreenConceder(); }

    public void hideScreenConceder() {
        if (isConcederActive()) {
            getScreenConceder().dispose();
            setScreenConceder(null);
        }
    }

    public Farstar getGame() { return game; }

    public OrthographicCamera getCamera() { return camera; }

    public Viewport getViewport() { return viewport; }

    public TableStage getTableMenu() { return tableStage; }

    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }

    public YXQuestionBox getScreenConceder() { return screenConceder; }

    public void setScreenConceder(YXQuestionBox screenConceder) { this.screenConceder = screenConceder; }

    public boolean isConcederActive() { return screenConceder != null; }
}
