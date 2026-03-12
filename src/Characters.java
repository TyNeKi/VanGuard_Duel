public class Characters {
    private String name;
    private int hp;
    private int mana;
    
    public Characters(String name, int hp, int mana) {
        this.name = name;
        this.hp = hp;
        this.mana = mana;
        
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getHp() {
        return this.hp;
    }
    
    public int getMana(){
        return this.mana;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public void setMana(int mana) {
        this.mana = mana;
    }
}
