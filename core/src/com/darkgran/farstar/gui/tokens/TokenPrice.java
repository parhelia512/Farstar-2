package com.darkgran.farstar.gui.tokens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.darkgran.farstar.gui.AssetLibrary;
import com.darkgran.farstar.gui.ColorPalette;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.AbilityManager;
import com.darkgran.farstar.gui.battlegui.AbilityPickerOption;
import com.darkgran.farstar.cards.AbilityInfo;
import com.darkgran.farstar.cards.AbilityStarter;
import com.darkgran.farstar.cards.Effect;
import com.darkgran.farstar.gui.TextDrawer;
import com.darkgran.farstar.util.SimpleVector2;

/**
 * Actually a group of pads: Handles the entire top-left corner (ie. ability-pads too)
 */
public class TokenPrice extends TokenPart {
    private Texture pad2;
    private SimpleVector2 textWH2;
    private Texture useMark;
    private Texture guardMark;
    private Texture reachMark;
    private Texture fsMark;
    private boolean showUseMark;
    private boolean showGuardMark;
    private boolean showReachMark;
    private boolean showFSMark;

    public TokenPrice(String fontPath, Token token) {
        super(fontPath, token);
    }

    @Override
    public boolean isEnabled() {
        return getToken().getTokenType() != TokenType.JUNK;
    }

    @Override
    public void setPad(TokenType tokenType) {
        setPad((Texture) Farstar.ASSET_LIBRARY.get(AssetLibrary.addTokenTypeAcronym("images/tokens/padE_", tokenType, true)+".png"));
        pad2 = Farstar.ASSET_LIBRARY.get(AssetLibrary.addTokenTypeAcronym("images/tokens/padM_", tokenType, true)+".png");
        if (tokenType != TokenType.PRINT) { useMark = Farstar.ASSET_LIBRARY.get(AssetLibrary.addTokenTypeAcronym("images/tokens/abiU_", tokenType, true)+".png"); }
        if (tokenType == TokenType.FLEET) {
            fsMark = Farstar.ASSET_LIBRARY.get("images/tokens/abiFS_F.png");
            guardMark = Farstar.ASSET_LIBRARY.get("images/tokens/abiG_F.png");
            reachMark = Farstar.ASSET_LIBRARY.get("images/tokens/abiR_F.png");
        }
    }

    @Override
    public void setupOffset() {
        setOffsetY(-getPad().getHeight());
        setOffsetX(getPad().getWidth());
    }

    @Override
    public void update() {
        String e = Integer.toString(getToken().getCard().getCardInfo().getEnergy());
        String m = Integer.toString(getToken().getCard().getCardInfo().getMatter());
        setTextWH(TextDrawer.getTextWH(getFont(), e));
        textWH2 = TextDrawer.getTextWH(getFont(), m);
        if (e.equals("1")) {
            getTextWH().x = getTextWH().x+3f;
        }
        if (m.equals("1")) {
            textWH2.x = textWH2.x+3f;
        }
        if (!(getToken() instanceof CardGFX)) {
            showUseMark = AbilityManager.hasStarter(getToken().getCard(), AbilityStarter.USE);
            for (AbilityInfo abilityInfo : getToken().getCard().getCardInfo().getAbilities()) {
                if (abilityInfo.getStarter() == AbilityStarter.NONE) {
                    for (Effect effect : abilityInfo.getEffects()) {
                        switch (effect.getEffectType()) {
                            case GUARD:
                                showGuardMark = true;
                                break;
                            case FIRST_STRIKE:
                                showFSMark = true;
                                break;
                            case REACH:
                                showReachMark = true;
                                break;
                        }
                    }
                }
            }
        } else {
            showFSMark = false;
            showReachMark = false;
            showUseMark = false;
            showGuardMark = false;
        }
    }

    @Override
    public void adjustTextWH() {}

    @Override
    public void draw(Batch batch) {
        if (isEnabled()) {
            float x = this.x - getPad().getWidth() + getOffsetX();
            if ((showUseMark && !isMouseOver()) || (showGuardMark && guardMark != null) || (showReachMark && reachMark != null) || (showFSMark && fsMark != null)) {
                if (showUseMark && useMark != null) {
                    batch.draw(useMark, x, this.y + getOffsetY());
                    x += getPad().getWidth();
                }
                if (showGuardMark && guardMark != null) {
                    batch.draw(guardMark, x, this.y + getOffsetY());
                    x += getPad().getWidth();
                }
                if (showReachMark && reachMark != null) {
                    batch.draw(reachMark, x, this.y + getOffsetY());
                    x += getPad().getWidth();
                }
                if (showFSMark && fsMark != null) { batch.draw(fsMark, x, this.y + getOffsetY()); }
            } else {
                int E = getResource(true, getToken() instanceof AbilityPickerOption ? TokenType.SUPPORT : getToken().getTokenType());
                if (E != 0 || showUseMark) {
                    String e = Integer.toString(E);
                    batch.draw(getPad(), x, this.y + getOffsetY());
                    drawText(getFont(), batch, x + getPad().getWidth() * 0.5f - getTextWH().x * 0.5f, this.y + getOffsetY() + getPad().getHeight() * 0.5f + getTextWH().y * 0.5f, e, ColorPalette.BLACKISH);
                    x += getPad().getWidth();
                }
                int M = getResource(false, getToken() instanceof AbilityPickerOption ? TokenType.SUPPORT : getToken().getTokenType());
                if (M != 0) {
                    String m = Integer.toString(M);
                    batch.draw(pad2, x, this.y + getOffsetY());
                    drawText(getFont(), batch, x + getPad().getWidth() * 0.5f - textWH2.x * 0.5f, this.y + getOffsetY() + getPad().getHeight() * 0.5f + textWH2.y * 0.5f, m, ColorPalette.BLACKISH);
                }
            }
        }
    }

    private int getResource(boolean energy, TokenType tokenType) {
        switch (tokenType) {
            case YARD:
            case JUNK:
            case FAKE:
            case HAND:
            case PRINT:
                return energy ? getToken().getCard().getCardInfo().getEnergy() : getToken().getCard().getCardInfo().getMatter();
            default:
            case MS:
            case SUPPORT:
            case FLEET:
                for (AbilityInfo abilityInfo : getToken().getCard().getCardInfo().getAbilities()) {
                    if (abilityInfo.getStarter() == AbilityStarter.USE) {
                        return energy ? abilityInfo.getResourcePrice().getEnergy() : abilityInfo.getResourcePrice().getMatter();
                    }
                }
                return 0;
        }
    }

    private boolean isMouseOver() {
        if (getToken() instanceof ClickToken) {
            return ((ClickToken) getToken()).getClickListener().isOver();
        }
        return false;
    }

}
