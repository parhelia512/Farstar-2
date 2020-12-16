package com.darkgran.farstar.battle;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.gui.*;
import com.darkgran.farstar.battle.players.*;
import com.darkgran.farstar.battle.players.Automaton;

import java.util.concurrent.ThreadLocalRandom;

public class Battle1v1 extends Battle {
    private final Player player1;
    private final Player player2;

    public Battle1v1(Player player1, Player player2) {
        super();
        //in future: pass the Players in parameters
        this.player1 = player1;
        this.player2 = player2;
        this.player1.getMs().setPlayer(player1);
        this.player1.getDeck().setPlayerOnAll(player1);
        this.player1.getYard().setPlayerOnAll(player1);
        this.player2.getMs().setPlayer(player2);
        this.player2.getDeck().setPlayerOnAll(player2);
        this.player2.getYard().setPlayerOnAll(player2);
        if (this.player1 instanceof Automaton) { ((Automaton) this.player1).setBattle(this); }
        if (this.player2 instanceof Automaton) { ((Automaton) this.player2).setBattle(this); }
    }

    @Override
    public BattleStage createBattleStage(Farstar game, Viewport viewport, BattleScreen battleScreen) {
        return new BattleStage1V1(game, viewport, battleScreen, new DuelMenu1v1(getCombatManager().getDuelManager()), player1, player2);
    }

    @Override
    public DuelManager createDuelManager() {
        return new DuelManager1v1();
    }

    @Override
    public void coinToss() {
        setWhoseTurn((ThreadLocalRandom.current().nextInt(0, 2) == 0) ? player1 : player2);
    }

    @Override
    public void passTurn() {
        setWhoseTurn(player1 == getWhoseTurn() ? player2 : player1);
    }

    @Override
    public void closeYards() {
        ((YardMenu) (player1.getShipyard().getCardListMenu())).setVisible(false);
        ((YardMenu) (player2.getShipyard().getCardListMenu())).setVisible(false);
    }

    @Override
    public void unMarkAllPossibilities() {
        getRoundManager().getPossibilityAdvisor().unMarkAll(player1, this);
        getRoundManager().getPossibilityAdvisor().unMarkAll(player2, this);
    }

    @Override
    public void startingCards() {
        if (getWhoseTurn() == player1) {
            player1.getHand().drawCards(player1.getDeck(), STARTING_CARDS_ATT);
            player2.getHand().drawCards(player2.getDeck(), STARTING_CARDS_DEF);
            player2.getHand().drawCards(BattleSettings.BONUS_CARD_ID, 1, player2);
        } else {
            player1.getHand().drawCards(player1.getDeck(), STARTING_CARDS_DEF);
            player1.getHand().drawCards(BattleSettings.BONUS_CARD_ID, 1, player1);
            player2.getHand().drawCards(player2.getDeck(), STARTING_CARDS_ATT);
        }
    }

    @Override
    public void setUsedForAllFleets(boolean used) {
        setUsedForFleet(player1, used);
        setUsedForFleet(player2, used);
    }

    @Override
    public void addGameOver(Player player) {
        getGameOvers().add(player);
        battleEnd();
    }

    @Override
    public void battleEnd() {
        super.battleEnd();
        System.out.println("Player #"+getWinnerID()+" wins!");
    }

    private int getWinnerID() {
        if (getGameOvers().contains(player1)) {
            return player2.getBattleID();
        } else {
            return player1.getBattleID();
        }
    }

    @Override
    public void tickEffects() {
        super.tickEffects();
        player1.getMs().checkEffects(getAbilityManager());
        player1.getFleet().checkEffectsOnAll(getAbilityManager());
        player1.getSupports().checkEffectsOnAll(getAbilityManager());
        player2.getMs().checkEffects(getAbilityManager());
        player2.getFleet().checkEffectsOnAll(getAbilityManager());
        player2.getSupports().checkEffectsOnAll(getAbilityManager());
    }

    @Override
    public Player[] getEnemies(Player player) {
        if (player == player1) {
            return new Player[]{player2};
        } else {
            return new Player[]{player1};
        }
    }
}
