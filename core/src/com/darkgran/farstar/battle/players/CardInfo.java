package com.darkgran.farstar.battle.players;

public class CardInfo {
    private final byte id;
    private final String name;
    private final CardType source;
    private int energy; //resource-price
    private int matter; //resource-price
    private int offense;
    private int defense;
    private TechType offenseType;
    private TechType defenseType;

    public CardInfo(byte id, String name, CardType source, int energy, int matter, int offense, int defense, TechType offenseType, TechType defenseType) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.energy = energy;
        this.matter = matter;
        this.offense = offense;
        this.defense = defense;
        this.offenseType = offenseType;
        this.defenseType = defenseType;
    }

    public CardInfo() {
        this.id = 0;
        this.name = "X";
        this.source = CardType.UPGRADE;
        this.energy = 0;
        this.matter = 0;
        this.offense = 0;
        this.defense = 0;
        this.offenseType = TechType.INFERIOR;
        this.defenseType = TechType.INFERIOR;
    }

    public byte getId() { return id; }

    public String getName() { return name; }

    public CardType getSource() { return source; }

    public int getEnergy() { return energy; }

    public int getMatter() { return matter; }

    public int getOffense() { return offense; }

    public int getDefense() { return defense; }

    public void setEnergy(int energy) { this.energy = energy; }

    public void setMatter(int matter) { this.matter = matter; }

    public void setOffense(int offense) { this.offense = offense; }

    public void setDefense(int defense) { this.defense = defense; }

    public TechType getOffenseType() { return offenseType; }

    public void setOffenseType(TechType offenseType) { this.offenseType = offenseType; }

    public TechType getDefenseType() { return defenseType; }

    public void setDefenseType(TechType defenseType) { this.defenseType = defenseType; }

}
