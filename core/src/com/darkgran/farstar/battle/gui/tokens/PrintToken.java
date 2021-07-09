package com.darkgran.farstar.battle.gui.tokens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.gui.BattleStage;
import com.darkgran.farstar.battle.gui.CardListMenu;
import com.darkgran.farstar.battle.players.cards.Card;
import com.darkgran.farstar.util.SimpleVector2;

import static com.darkgran.farstar.battle.BattleScreen.DEBUG_RENDER;

/**
 * Used for "card-zoom" etc.
 */
public class PrintToken extends Token {
    private Texture cardPic;
    private TokenType targetType;
    private SimpleVector2 targetXY = new SimpleVector2(0, 0);

    public PrintToken(Card card, float x, float y, BattleStage battleStage, CardListMenu cardListMenu, boolean connectCard) {
        super(card, x, y, battleStage, cardListMenu, TokenType.PRINT, false, connectCard);
        cardPic = Farstar.ASSET_LIBRARY.get("images/tokens/card_Z.png");
        setTouchable(Touchable.disabled);
    }

    public void enable(Card card, TokenType targetType, SimpleVector2 targetXY) {
        if (getCard() != card) {
            setup(card, targetType, targetXY);
            if (getGlowG() == null) { setGlows(); }
        }
    }

    @Override
    public void setGlows() {
        setGlowG(Farstar.ASSET_LIBRARY.get("images/tokens/glowG_Z.png"));
        setGlowY(Farstar.ASSET_LIBRARY.get("images/tokens/glowY_Z.png"));
        setGlowOffsetX(-getGlowG().getWidth()/2f+getFrame().getWidth()/2f);
        setGlowOffsetY(-getGlowG().getHeight()/2f+cardPic.getHeight()/2f);
    }

    @Override
    public void setup(Card card, TokenType targetType, SimpleVector2 targetXY) {
        super.setup(card, targetType, targetXY);
        if (card.isPossible()) {
            setGlowState(GlowState.POSSIBLE);
        } else {
            setGlowState(GlowState.DIM);
        }
        this.targetType = targetType;
        this.targetXY = targetXY;
        shiftPosition();
    }

    public void shiftPosition() { }

    public void disable() {
        setCard(null);
    }

    @Override
    public void draw(Batch batch) {
        if (getCard() != null) {
            drawGlows(batch);
            batch.draw(cardPic, getX(), getY());
            drawPortrait(batch);
            getTokenDefense().draw(batch);
            getTokenOffense().draw(batch);
            getTokenPrice().draw(batch);
            if (DEBUG_RENDER) {
                debugRender(batch);
            }
        }
    }

    @Override
    protected void drawPortrait(Batch batch) {
        if (getPortrait() != null) { batch.draw(getPortrait(), getX(), getY()+cardPic.getHeight()-getPortrait().getHeight()+HandToken.PORTRAIT_OFFSET_Y); }
        if (getFrame() != null) { batch.draw(getFrame(), getX(), getY()+cardPic.getHeight()-getFrame().getHeight()+HandToken.PORTRAIT_OFFSET_Y); }
    }

    public TokenType getTargetType() {
        return targetType;
    }

    public SimpleVector2 getTargetXY() {
        return targetXY;
    }

    public Texture getCardPic() {
        return cardPic;
    }

}
