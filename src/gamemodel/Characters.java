package gamemodel;

public class Characters {
    private String name, role;
    private int hp, maxHp, mana, maxMana, manaPerTurn;
    private double nextDamageReduction = 0.0, damageDealtMultiplier = 1.0;
    private Skills[] skills;

    public Characters(String name, String role, int hp, int mana, int manaPerTurn, Skills[] skills) {
        this.name = name; this.role = role;
        this.hp = hp; this.maxHp = hp;
        this.mana = mana; this.maxMana = mana;
        this.manaPerTurn = manaPerTurn;
        this.skills = skills;
    }

    public void updateHp(int val) {
        this.hp = Math.min(maxHp, Math.max(0, hp + val));
    }
    public void updateMana(int val) {
        this.mana = Math.min(maxMana, Math.max(0, mana + val));
    }


    public String getName() {
        return name;
    }
    public int getHp() {
        return hp;
    }
    public int getMaxHp() {
        return maxHp;
    }
    public int getMana() {
        return mana;
    }
    public int getMaxMana() {
        return maxMana;
    }
    public int getManaPerTurn() {
        return manaPerTurn;
    }
    public Skills[] getSkills() {
        return skills;
    }
    public double getNextDamageReduction() {
        return nextDamageReduction;
    }
    public void setNextDamageReduction(double r) {
        this.nextDamageReduction = r;
    }
    public double getDamageDealtMultiplier() {
        return damageDealtMultiplier;
    }
    public void setDamageDealtMultiplier(double m) {
        this.damageDealtMultiplier = m;
    }
}