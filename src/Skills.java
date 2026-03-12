public class Skills {
    private String skillName;
    private int manaCost;
    private int minDamage;
    private int maxDamage;
    
    public Skills(String skillName, int manaCost, int minDamage, int maxDamage) {
        this.skillName = skillName;
        this.manaCost = manaCost;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public int getManaCost() {
        return this.manaCost;
    }
    
    public int getMinDamage() {
        return this.minDamage;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }   

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }


}
