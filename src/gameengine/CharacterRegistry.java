package gameengine;

import gamemodel.Characters;
import gamemodel.Skills;

public class CharacterRegistry {
    public static Characters getCharacter(String name) {
        switch (name) {
            case "Tyron":
                return new Characters("Tyron", "Energy Sentinel", 300, 200, 15, new Skills[]{
                        new Skills("Precision Strike", 25, 55, 75),
                        new Skills("Energy Guard", 40, 0, 0),
                        new Skills("Overdrive Pulse", 60, 95, 125)

                }, "male");
            case "Lance":
                return new Characters("Lance", "Light Bringer", 320, 200, 20, new Skills[]{
                        new Skills("Holy Nova Smash", 25, 55, 75),
                        new Skills("Light Pillar", 35, 0, 0),
                        new Skills("Supernova Strike", 65, 100, 130)
                }, "male");
            case "Adrian":
                return new Characters("Adrian", "Abyssal Blade", 300, 180, 15, new Skills[]{
                        new Skills("Abyss Slash", 25, 60, 80),
                        new Skills("Void Rend", 35, 85, 115),
                        new Skills("Oblivion Edge", 55, 110, 140)
                }, "male");
            case "Clark":
                return new Characters("Clark", "Kyuoka Blade Dance", 300, 220, 20, new Skills[]{
                        new Skills("Elemental Strike", 25, 55, 75),
                        new Skills("Elemental Burst", 50, 90, 120),
                        new Skills("Hydro Guard", 40, 0, 0)
                }, "male");
            case "Raze":
                return new Characters("Raze", "Inferno Berserker", 320, 160, 10, new Skills[]{
                        new Skills("Flame Cleave", 25, 65, 90),
                        new Skills("Scorch Rush", 35, 90, 120),
                        new Skills("Cataclysm Blaze", 60, 115, 145)
                }, "male");
            case "Marie":
                return new Characters("Marie", "Arcane Tempest", 300, 200, 15, new Skills[]{
                        new Skills("Arcane Bolt", 25, 60, 80),
                        new Skills("Mana Burn", 35, 65, 85),
                        new Skills("Tempest Surge", 60, 100, 130)
                }, "female");
            case "Alyana":
                return new Characters("Alyana", "Radiant Aegis", 300, 220, 20, new Skills[]{
                        new Skills("Light Spear", 25, 60, 85),
                        new Skills("Radiant Blessing", 40, 0, 0),
                        new Skills("Divine Retribution", 60, 95, 125)
                }, "female");
            case "Katarina":
                return new Characters("Katarina", "Shadow Dominator", 280, 200, 15, new Skills[]{
                        new Skills("Shadow Slash", 25, 65, 90),
                        new Skills("Dark Surge", 40, 90, 120),
                        new Skills("Soul Reaver", 60, 110, 140)
                }, "female");

            default:
                return null;
        }
    }

    public static String[] getAllNames() {
        return new String[]{"Tyron", "Lance", "Adrian", "Clark", "Raze", "Marie", "Alyana", "Katarina"};
    }
}