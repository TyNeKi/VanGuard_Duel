package Data;

import Models.Characters;
import Models.Skills;

public class CharacterRegistry {
    public static Characters getCharacter(String name) {
        switch (name) {
            case "Tyron":
                return new Characters("Tyron", "Energy Sentinel", 500, 200, 15, new Skills[]{new Skills("Precision Strike", 25, 35, 55), new Skills("Energy Guard", 40, 0, 0), new Skills("Overdrive Pulse", 60, 75, 105)});
            case "Lance":
                return new Characters("Lance", "Light Bringer", 520, 200, 20, new Skills[]{new Skills("Holy Nova Smash", 25, 35, 55), new Skills("Light Pillar", 35, 0, 0), new Skills("Supernova Strike", 65, 80, 110)});
            case "Adrian":
                return new Characters("Adrian", "Abyssal Blade", 500, 180, 15, new Skills[]{new Skills("Abyss Slash", 25, 40, 60), new Skills("Void Rend", 35, 65, 95), new Skills("Oblivion Edge", 55, 90, 120)});
            case "Alyana":
                return new Characters("Alyana", "Radiant Aegis", 500, 220, 20, new Skills[]{new Skills("Light Spear", 25, 40, 65), new Skills("Radiant Blessing", 40, 0, 0), new Skills("Divine Retribution", 60, 75, 105)});
            case "Clark":
                return new Characters("Clark", "Sustained Fighter", 500, 220, 20, new Skills[]{new Skills("Elemental Strike", 25, 25, 55), new Skills("Elemental Burst",50, 70,100 ), new Skills("Elemental Strike", 50, 70, 110)});
            default:
                return null;
        }
    }
    public static String[] getAllNames() { return new String[]{"Tyron", "Lance", "Adrian", "Alyana", "Clark" +
            "-"}; }
}