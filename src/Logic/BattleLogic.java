package Logic;

import java.util.Random;

import gamemodel.Characters;
import gamemodel.Skills;

public class BattleLogic {
    private static final Random rand = new Random();

    public static int calculateDamage(Characters atk, Characters def, Skills s) {
        int base = rand.nextInt((s.getMaxDamage() - s.getMinDamage()) + 1) + s.getMinDamage();
        return (int) ((base * atk.getDamageDealtMultiplier()) * (1.0 - def.getNextDamageReduction()));
    }


    public static void processEffects(Skills s, Characters user, Characters target, int dmg) {
        String n = s.getSkillName();


        if (n.contains("Guard"))
            user.setNextDamageReduction(0.45);
        if (n.equals("Light Pillar"))
            target.setDamageDealtMultiplier(0.60);
        if (n.equals("Mana Burn"))
            target.updateMana(-20);
        if (n.equals("Radiant Blessing"))
            user.updateHp(85);
        if (n.contains("Reaver"))
            user.updateHp((int)(dmg * 0.25));

        target.updateHp(-dmg);
        user.setDamageDealtMultiplier(1.0);
        target.setNextDamageReduction(0.0);
    }
}