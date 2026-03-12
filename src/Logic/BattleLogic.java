package Logic;

import Models.Characters;
import Models.Skills;
import java.util.Random;

public class BattleLogic {
    private static final Random rand = new Random();

    public static int calculateDamage(Characters atk, Characters def, Skills s) {
        int base = rand.nextInt((s.getMaxDamage() - s.getMinDamage()) + 1) + s.getMinDamage();
        double mult = atk.getDamageDealtMultiplier();
        double red = def.getNextDamageReduction();
        return (int) ((base * mult) * (1.0 - red));
    }

    public static void processEffects(Skills s, Characters user, Characters target, int dmg) {
        String n = s.getSkillName();
        if (n.contains("Guard")) user.setNextDamageReduction(0.40);
        if (n.equals("Light Pillar")) target.setDamageDealtMultiplier(0.70);
        if (n.equals("Mana Burn")) target.setMana(target.getMana() - 20);
        if (n.equals("Radiant Blessing")) user.setHp(user.getHp() + 85);
        if (n.contains("Reaver") || n.contains("Retribution")) user.setHp(user.getHp() + (int)(dmg * 0.25));

        target.setHp(target.getHp() - dmg);
        user.setDamageDealtMultiplier(1.0);
        target.setNextDamageReduction(0.0);
    }
}