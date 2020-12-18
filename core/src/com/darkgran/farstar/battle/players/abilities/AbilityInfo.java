package com.darkgran.farstar.battle.players.abilities;

import com.darkgran.farstar.battle.players.ResourcePrice;

import java.util.ArrayList;

public class AbilityInfo {
    private final AbilityStarter starter;
    private final AbilityTargets targets;
    private final ResourcePrice resourcePrice;
    private final ArrayList<Effect> effects;

    public AbilityInfo(AbilityStarter starter, ArrayList<Effect> effects, ResourcePrice resourcePrice, AbilityTargets targets) {
        this.starter = starter;
        this.effects = effects;
        this.resourcePrice = resourcePrice;
        this.targets = targets;
    }

    public AbilityInfo() {
        this.starter = AbilityStarter.DEPLOY;
        this.effects = new ArrayList<>();
        this.resourcePrice = new ResourcePrice();
        this.targets = AbilityTargets.NONE;
    }

    public boolean isPureOffenseChanger() { //for disabling upgrading Offense on motherships
        boolean foundOffense = false;
        for (Effect effect : effects) {
            if (effect.getEffectType() != null && effect.getEffectType() == EffectType.CHANGE_STAT && effect.getEffectInfo() != null && effect.getEffectInfo().size() >= 2 && effect.getEffectInfo().get(0) != null && effect.getEffectInfo().get(1) != null) {
                EffectTypeSpecifics.ChangeStatType changeStatType = EffectTypeSpecifics.ChangeStatType.valueOf(effect.getEffectInfo().get(0).toString());
                if (changeStatType == EffectTypeSpecifics.ChangeStatType.OFFENSE_TYPE || changeStatType == EffectTypeSpecifics.ChangeStatType.OFFENSE) {
                    foundOffense = true;
                } else {
                    return false;
                }
            }
        }
        return foundOffense;
    }

    public AbilityStarter getStarter() { return starter; }

    public ResourcePrice getResourcePrice() { return resourcePrice; }

    public ArrayList<Effect> getEffects() { return effects; }

    public AbilityTargets getTargets() { return targets; }

}
