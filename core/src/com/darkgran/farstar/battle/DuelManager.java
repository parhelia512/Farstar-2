package com.darkgran.farstar.battle;

import com.darkgran.farstar.battle.gui.DuelMenu;
import com.darkgran.farstar.battle.gui.Token;
import com.darkgran.farstar.battle.players.Player;

public abstract class DuelManager {
    private DuelMenu duelMenu;
    private boolean active = false;
    private Token attacker;
    private Token defender;
    private Player[] playersA;
    private Player[] playersD;

    public void launchDuel(Token attacker, Token defender, Player[] playersA, Player[] playersD) { }

    public void receiveDuelMenu(DuelMenu duelMenu) { this.duelMenu = duelMenu; }

    public DuelMenu getDuelMenu() { return duelMenu; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public Token getAttacker() { return attacker; }

    public Token getDefender() { return defender; }

    public Player[] getPlayersA() { return playersA; }

    public Player[] getPlayersD() { return playersD; }


}
