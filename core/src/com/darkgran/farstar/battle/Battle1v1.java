package com.darkgran.farstar.battle;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkgran.farstar.Farstar;
import com.darkgran.farstar.battle.cards.Card;
import com.darkgran.farstar.battle.cards.Deck;
import com.darkgran.farstar.battle.cards.Shipyard;
import com.darkgran.farstar.battle.gui.GUI;

import java.util.concurrent.ThreadLocalRandom;

public class Battle1v1 extends Battle {
    private final Player player1;
    private final Player player2;

    public Battle1v1(Farstar game, Viewport viewport) {
        super();
        //in future: pass the Players in parameters
        player1 = new Player((byte) 1, STARTING_ENERGY, STARTING_MATTER, new Card(), new Deck(), new Shipyard());
        player2 = new Player((byte) 2, STARTING_ENERGY, STARTING_MATTER, new Card(), new Deck(), new Shipyard());
    }

    @Override
    public void coinToss() {
        setWhoseTurn((ThreadLocalRandom.current().nextInt(0, 2) == 0) ? player1 : player2);
    }

    @Override
    public void startingCards() {
        if (getWhoseTurn() == player1) {
            player1.drawCards(STARTING_CARDS_ATT);
            player2.drawCards(STARTING_CARDS_DEF);
        } else {
            player1.drawCards(STARTING_CARDS_DEF);
            player2.drawCards(STARTING_CARDS_ATT);
        }
    }

}