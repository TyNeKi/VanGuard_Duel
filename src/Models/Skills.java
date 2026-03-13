package Models;

public class Skills {
    private String skillName;
    private int manaCost, minDamage, maxDamage;

    public Skills(String skillName, int manaCost, int minDamage, int maxDamage) {
        this.skillName = skillName;
        this.manaCost = manaCost;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public String getSkillName() {
        return skillName;
    }
    public int getManaCost() {
        return manaCost;
    }
    public int getMinDamage() {
        return minDamage;
    }
    public int getMaxDamage() {
        return maxDamage;
    }
}