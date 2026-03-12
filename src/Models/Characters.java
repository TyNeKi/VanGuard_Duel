package Models;

public class Characters {
    private String name, role;
    private int hp, maxHp, mana, maxMana, manaPerTurn;
    private double nextDamageReduction = 0.0;
    private double damageDealtMultiplier = 1.0;
    private Skills[] skills;

    public Characters(String name, String role, int hp, int mana, int manaPerTurn, Skills[] skills) {
        this.name = name; this.role = role;
        this.hp = hp; this.maxHp = hp;
        this.mana = mana; this.maxMana = mana;
        this.manaPerTurn = manaPerTurn;
        this.skills = skills;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }
    public int getManaPerTurn() { return manaPerTurn; }
    public Skills[] getSkills() { return skills; }
    public double getNextDamageReduction() { return nextDamageReduction; }
    public double getDamageDealtMultiplier() { return damageDealtMultiplier; }

    public void setHp(int hp) { this.hp = Math.min(maxHp, Math.max(0, hp)); }
    public void setMana(int mana) { this.mana = Math.min(maxMana, Math.max(0, mana)); }
    public void setNextDamageReduction(double r) { this.nextDamageReduction = r; }
    public void setDamageDealtMultiplier(double m) { this.damageDealtMultiplier = m; }
}