package Models;

public class Characters {
    private String name;
    private String role;
    private int hp;
    private int maxHp;
    private int mana;
    private int maxMana;
    private int manaPerTurn;
    private Skills[] skills;

    public Characters(String name, String role, int hp, int mana, int manaPerTurn, Skills[] skills) {
        this.name = name;
        this.role = role;
        this.hp = hp;
        this.maxHp = hp;
        this.mana = mana;
        this.maxMana = mana;
        this.manaPerTurn = manaPerTurn;
        this.skills = skills;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }
    public int getManaPerTurn() { return manaPerTurn; }
    public Skills[] getSkills() { return skills; }

    public void setHp(int hp) { this.hp = hp; }
    public void setMana(int mana) { this.mana = mana; }
}