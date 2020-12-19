package com.darkgran.farstar.battle.players;

import com.darkgran.farstar.battle.DuelManager;
import com.darkgran.farstar.battle.gui.*;
import com.darkgran.farstar.battle.gui.tokens.Token;
import com.darkgran.farstar.battle.players.abilities.*;
import com.darkgran.farstar.battle.players.cards.*;

import java.util.ArrayList;

import static com.darkgran.farstar.battle.BattleSettings.BONUS_CARD_ID;

/**
 *  "Just Play Something":
 *  -- No sensors beyond PossibilityAdvisor
 *  -- No planning (atm not even the frame for it)
 */
public class Automaton extends Bot {

    public Automaton(byte battleID, int energy, int matter, Mothership ms, Deck deck, Yard yard, BotTier botTier) {
        super(battleID, energy, matter, ms, deck, yard, botTier);
    }

    //---------------//
    //-PLAYING-CARDS-//
    //---------------//

    @Override
    public void turn() {
        if (!isDisposed()) {
            super.turn();
            PossibilityInfo bestPossibility = getTurnPossibility();
            if (bestPossibility != null) {
                report("Playing a card: " + bestPossibility.getCard().getCardInfo().getName());
                boolean success;
                if (isDeploymentMenu(bestPossibility.getMenu()) || bestPossibility.getCard().getCardInfo().getCardType() == CardType.MS) {
                    success = useAbility(bestPossibility.getCard(), bestPossibility.getMenu());
                } else {
                    success = deploy(bestPossibility.getCard(), bestPossibility.getMenu(), getBestPosition(bestPossibility.getCard(), bestPossibility.getMenu(), getBattle().getRoundManager().getPossibilityAdvisor().getTargetMenu(bestPossibility.getCard(), this)));
                }
                if (!success && !isPickingAbility() && !isPickingTarget()) {
                    report("turn() failed!");
                    cancelTurn();
                } else {
                    delayedTurn();
                }
            } else {
                report("No possibilities.");
                delayedEndTurn();
            }
        }
    }

    @Override
    public DropTarget getDropTarget(CardType cardType) {
        if (cardType == CardType.SUPPORT) {
            return (SupportMenu) getSupports().getCardListMenu();
        } else {
            if (!CardType.isShip(cardType) && getFleet().isEmpty()) {
                return (DropTarget) getMs().getToken();
            } else {
                return getFleet().getFleetMenu();
            }
        }
    }

    public int getBestPosition(Card card, BaseMenu sourceMenu, BaseMenu targetMenu) {
        if (CardType.isShip(card.getCardInfo().getCardType())) {
            if (targetMenu.isEmpty()) {
                return 3;
            } else {
                return 2;
            }
        } else if (targetMenu instanceof FleetMenu && (card.getCardInfo().getCardType() == CardType.UPGRADE || card.isTactic())){
            FleetMenu fleetMenu = (FleetMenu) targetMenu;
            Token ally = getAlliedTarget(cardToToken(card, sourceMenu), null);
            for (int i = 0; i < fleetMenu.getShips().length; i++) {
                if (fleetMenu.getShips()[i] != null) {
                    if (fleetMenu.getShips()[i].getCard() == ally.getCard()) {
                        return i;
                    }
                }
            }
            return -1;
        } else {
            return 3;
        }
    }

    public PossibilityInfo getTurnPossibility() {
        ArrayList<PossibilityInfo> possibilities = getBattle().getRoundManager().getPossibilityAdvisor().getPossibilities(this, getBattle());
        if (possibilities.size() > 0) {
            //1. Have at least one defender
            if (getFleet().isEmpty()) {
                PossibilityInfo ship = null;
                for (PossibilityInfo possibilityInfo : possibilities) {
                    if (CardType.isShip(possibilityInfo.getCard().getCardInfo().getCardType())) {
                        if (ship == null) { ship = possibilityInfo; }
                        if (getBattle().getAbilityManager().hasAttribute(possibilityInfo.getCard(), EffectType.GUARD)) { //Guard Preference
                            return possibilityInfo;
                        }
                    }
                }
                if (ship != null) {
                    return ship;
                }
            }
            //2. Play the first playable thing that's not "nonsense"
            for (PossibilityInfo possibilityInfo : possibilities) {
                if (!isNonsense(possibilityInfo)) {
                    return possibilityInfo;
                }
            }
        }
        return null;
    }

    public boolean isNonsense(PossibilityInfo possibilityInfo) {
        return (possibilityInfo.getCard().isTactic() != getBattle().getCombatManager().isActive()) || abilityNonsense(possibilityInfo.getCard());
    }

    public boolean abilityNonsense(Card card) {
        for (AbilityInfo ability : card.getCardInfo().getAbilities()) {
            for (Effect effect : ability.getEffects()) {
                if (effect != null && effect.getEffectType() != null) {
                    switch (effect.getEffectType()) {
                        case CHANGE_STAT:
                            if (effect.getEffectInfo() != null && effect.getEffectInfo().size() >= 2 && effect.getEffectInfo().get(0) != null && effect.getEffectInfo().get(1) != null) {
                                Object changeInfo = effect.getEffectInfo().get(1);
                                EffectTypeSpecifics.ChangeStatType changeStatType = EffectTypeSpecifics.ChangeStatType.valueOf(effect.getEffectInfo().get(0).toString());
                                Token allyToken;
                                Card ally;
                                //Validate change of Type (color)
                                if (changeStatType == EffectTypeSpecifics.ChangeStatType.OFFENSE_TYPE || changeStatType == EffectTypeSpecifics.ChangeStatType.DEFENSE_TYPE) {
                                    if (getBattle().getCombatManager().isActive()) { //COMBAT ONLY
                                        allyToken = getAllyInDuel();
                                        ally = allyToken.getCard();
                                        Card enemy = getBattle().getCombatManager().getDuelManager().getOpponent(allyToken).getCard();
                                        if (techTypeNonsense(ally, enemy, changeStatType, changeInfo)) {
                                            return true;
                                        }
                                        if (ability.isPurelyTypeChange()) {
                                            return (changeStatType == EffectTypeSpecifics.ChangeStatType.OFFENSE_TYPE && (ally.getCardInfo().getOffense() <= 1 || enemy.getCardInfo().getDefense() < ally.getCardInfo().getOffense())) || (changeStatType == EffectTypeSpecifics.ChangeStatType.DEFENSE_TYPE && (enemy.getCardInfo().getOffense() <= 1 || ally.getCardInfo().getDefense() < enemy.getCardInfo().getOffense()));
                                        }
                                    } else { //OUTSIDE COMBAT
                                        //in-future: don't allow Upgrades to certain Types (= based on what the enemy has the most)
                                    }
                                //Validate Ability change
                                } else if (changeStatType == EffectTypeSpecifics.ChangeStatType.ABILITY){
                                    EffectType effectType = EffectType.valueOf(changeInfo.toString());
                                    switch (effectType) {
                                        case FIRST_STRIKE:
                                            if (getBattle().getCombatManager().isActive()) { //COMBAT ONLY
                                                allyToken = getAllyInDuel();
                                                ally = allyToken.getCard();
                                                if (getBattle().getCombatManager().getDuelManager().getStrikePriority() != null && getBattle().getCombatManager().getDuelManager().getStrikePriority() == ally) {
                                                    return true;
                                                }
                                            } else { //OUTSIDE COMBAT
                                                for (Ship ship : getFleet().getShips()) {
                                                    if (ship != null && !getBattle().getAbilityManager().hasAttribute(ship, EffectType.FIRST_STRIKE)) {
                                                        return false;
                                                    }
                                                }
                                                return true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            break;
                        case REPAIR:
                            //TODO findWounded
                            break;
                        case CHANGE_RESOURCE:
                            //TODO dont go energy when low on matter
                            break;

                    }
                }
            }
        }
        return false;
    }

    private Token getAllyInDuel() {
        if (getBattle().getCombatManager().getDuelManager().isActive()) {
            Token attacker = getBattle().getCombatManager().getDuelManager().getAttacker();
            Token defender = getBattle().getCombatManager().getDuelManager().getDefender();
            if (attacker.getCard().getPlayer() == this) {
                return attacker;
            } else {
                return defender;
            }
        }
        return null;
    }

    public boolean techTypeNonsense(Card ally, Card enemy, EffectTypeSpecifics.ChangeStatType changeStatType, Object changeInfo) {
        return (changeStatType == EffectTypeSpecifics.ChangeStatType.OFFENSE_TYPE && DuelManager.noneToInferior(ally.getCardInfo().getOffenseType()) != TechType.INFERIOR && ally.getCardInfo().getOffenseType() != enemy.getCardInfo().getDefenseType()) || (changeStatType == EffectTypeSpecifics.ChangeStatType.DEFENSE_TYPE && ally.getCardInfo().getDefenseType() == enemy.getCardInfo().getOffenseType());
    }

    @Override
    //asked to pick Target for ability
    public void chooseTargets(Token token, AbilityInfo ability) {
        Token target = null;
        if (ability != null && ability.getTargets() != null) {
            setPickingTarget(true);
            switch (ability.getTargets()) {
                case ANY_ALLY:
                case ALLIED_FLEET:
                    EffectType attribute = null;
                    if (ability.getStarter() == AbilityStarter.NONE) { //in-future: check for changing all abilities (no ability givers in game atm except the ones with Attribute)
                        for (Effect effect : ability.getEffects()) {
                            if (effect.getEffectType() != null && effect.getEffectType() == EffectType.FIRST_STRIKE && effect.getEffectType() == EffectType.GUARD) {
                                attribute = effect.getEffectType();
                                break;
                            }
                        }
                    }
                    target = getAlliedTarget(token, attribute); //in-future: check against field of attributes instead of the first attribute (again, does not matter with "prototype cards")
                    break;
                case ANY: //expects that Upgrades cannot be used on enemies, ergo ANY must mean ANY_ENEMY (it's "ANY" only for the whims of human player)
                case ANY_ENEMY:
                case ENEMY_FLEET:
                    target = getEnemyTarget(token, false);
                    break;
            }
        }
        if (target != null) {
            getBattle().getRoundManager().processTarget(target);
        } else {
            report("chooseTargets() failed!");
            cancelTurn();
        }
    }

    @Override
    public Token getAlliedTarget(Token caster, EffectType effectType) { //todo
        if (getFleet().isEmpty()) {
            if (!getBattle().getAbilityManager().hasAttribute(getMs(), effectType)) {
                return getMs().getToken();
            }
        } else {
            Ship strongestShip = null;
            for (Ship ship : getFleet().getShips()) {
                if (ship != null) {
                    if (strongestShip == null || isBiggerShip(ship, strongestShip)) {
                        if (!getBattle().getAbilityManager().hasAttribute(strongestShip, effectType)) {
                            strongestShip = ship;
                        }
                    }
                }
            }
            if (strongestShip != null) {
                return strongestShip.getToken();
            }
        }
        return null;
    }

    @Override
    public Token getEnemyTarget(Token attacker, boolean checkReach) {
        Player[] enemies = getBattle().getEnemies(this);
        Token picked = null;
        Ship weakestShip = null;
        for (Player enemy : enemies) {
            if (picked == null && enemy.getFleet().isEmpty()) {
                picked = enemy.getMs().getToken();
            } else {
                for (Ship ship : enemy.getFleet().getShips()) {
                    if (ship != null) {
                        if ((weakestShip == null || isBiggerShip(weakestShip, ship)) && (!checkReach || getBattle().getCombatManager().canReach(attacker, ship.getToken(), this.getFleet()))) {
                            weakestShip = ship;
                            picked = ship.getToken();
                        }
                    }
                }
            }
        }
        return picked;
    }

    private boolean isBiggerShip(Ship A, Ship B) { //return A>B (in-future: consider abilities)
        return A.getCardInfo().getOffense() + A.getCardInfo().getDefense() - A.getDamage() > B.getCardInfo().getOffense() + B.getCardInfo().getDefense() - B.getDamage();
    }

    @Override
    //asked to pick an ability
    public void pickAbility(Token caster, Token target, DropTarget dropTarget, ArrayList<AbilityInfo> options) {
        if (options.size() > 0) {
            setPickingAbility(true);
            //if (caster.getCard().getCardInfo().getId() == BONUS_CARD_ID) {
                getBattle().getRoundManager().processPick(options.get(1));
            //} else {
            //    getBattle().getRoundManager().processPick(options.get(0));
            //}
        } else {
            report("pickAbility() failed!");
            cancelTurn();
        }
    }

    //--------//
    //-COMBAT-//
    //--------//

    @Override
    public void combat() {
        super.combat();
        if (getBattle().getCombatManager().isActive()) {
            for (Ship ship : getFleet().getShips()) {
                if (ship != null && !ship.haveFought()) {
                    delayedLaunchDuel(ship);
                    break;
                }
            }
        }
    }

    @Override
    public void duel(DuelOK duelOK) { //atm expects all Tactics to be meant for allies
        super.duel(duelOK);
        boolean success = false;
        PossibilityInfo bestPossibility = getDuelPossibility();
        if (bestPossibility != null) {
            report("Playing a tactic: " + bestPossibility.getCard().getCardInfo().getName());
            int position = -1;
            for (int i = 0; i < getFleet().getFleetMenu().getShips().length; i++) {
                if (getFleet().getFleetMenu().getShips()[i] != null) {
                    if (getFleet().getFleetMenu().getShips()[i].getCard().isInDuel()) {
                        position = i;
                        break;
                    }
                }
            }
            if (deploy(bestPossibility.getCard(), bestPossibility.getMenu(), position)) {
                success = true;
                delayedDuel(duelOK);
            } else {
                report("Failed to deploy the tactic!");
                success = false;
            }
        } else {
            report("No duel possibilities.");
        }
        if (!success) { duelReady(duelOK); }
    }

    public PossibilityInfo getDuelPossibility() {
        ArrayList<PossibilityInfo> possibilities = getBattle().getRoundManager().getPossibilityAdvisor().getPossibilities(this, getBattle());
        if (possibilities.size() > 0) {
            //1. Play the first playable thing that's not "nonsense"
            for (PossibilityInfo possibilityInfo : possibilities) {
                if (!isNonsense(possibilityInfo)) {
                    return possibilityInfo;
                }
            }
        }
        return null;
    }

    //------//
    //-MISC-//
    //------//

    @Override
    public void gameOver(int winnerID) {
        if (winnerID == getBattleID()) {
            report("Wow, I've won! How did I do that?");
        }
        super.gameOver(winnerID);
    }

}
