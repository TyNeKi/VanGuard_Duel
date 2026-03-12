package GUIChooseChar;

import javax.swing.*;
import java.awt.*;
import GUIStart.GUIStartScreen;
import GUIBattle.GUIBattleScreen;
import Models.Characters;
import Models.Skills;

public class GUICharacterSelection extends JFrame {

    public GUICharacterSelection() {
        setTitle("Select Your Character");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("CHOOSE YOUR VANGUARD", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 40));
        add(header, BorderLayout.NORTH);

        JPanel charPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        charPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        String[] names = {"Tyron", "Lance", "Adrian", "Clark", "Raze", "Marie", "Alyana", "Katarina"};

        for (String name : names) {
            JButton charBtn = new JButton(name);
            charBtn.setFont(new Font("Arial", Font.BOLD, 18));
            charBtn.addActionListener(e -> {
                new GUIBattleScreen(getCharacterData(name)).setVisible(true);
                dispose();
            });
            charPanel.add(charBtn);
        }

        add(charPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            new GUIStartScreen().setVisible(true);
            dispose();
        });
        add(backBtn, BorderLayout.SOUTH);
    }

    private Characters getCharacterData(String name) {
        switch (name) {
            case "Tyron": return new Characters("Tyron", "Energy Sentinel", 500, 200, 15, new Skills[]{new Skills("Precision Strike", 25, 35, 55), new Skills("Energy Guard", 40, 0, 0), new Skills("Overdrive Pulse", 60, 75, 105)});
            case "Lance": return new Characters("Lance", "Light Bringer", 520, 200, 20, new Skills[]{new Skills("Holy Nova Smash", 25, 35, 55), new Skills("Light Pillar", 35, 0, 0), new Skills("Supernova Strike", 65, 80, 110)});
            case "Adrian": return new Characters("Adrian", "Abyssal Blade", 500, 180, 15, new Skills[]{new Skills("Abyss Slash", 25, 40, 60), new Skills("Void Rend", 35, 65, 95), new Skills("Oblivion Edge", 55, 90, 120)});
            case "Clark": return new Characters("Clark", "Kyuoka Blade Dance", 500, 220, 20, new Skills[]{new Skills("Elemental Strike", 25, 35, 55), new Skills("Elemental Burst", 50, 70, 100), new Skills("Hydro Guard", 40, 0, 0)});
            case "Raze": return new Characters("Raze", "Inferno Berserker", 520, 160, 10, new Skills[]{new Skills("Flame Cleave", 25, 45, 70), new Skills("Scorch Rush", 35, 70, 100), new Skills("Cataclysm Blaze", 60, 95, 125)});
            case "Marie": return new Characters("Marie", "Arcane Tempest", 500, 200, 15, new Skills[]{new Skills("Arcane Bolt", 25, 40, 60), new Skills("Mana Burn", 35, 45, 65), new Skills("Tempest Surge", 60, 80, 110)});
            case "Alyana": return new Characters("Alyana", "Radiant Aegis", 500, 220, 20, new Skills[]{new Skills("Light Spear", 25, 40, 65), new Skills("Radiant Blessing", 40, 0, 0), new Skills("Divine Retribution", 60, 75, 105)});
            case "Katarina": return new Characters("Katarina", "Shadow Dominator", 480, 200, 15, new Skills[]{new Skills("Shadow Slash", 25, 45, 70), new Skills("Dark Surge", 40, 70, 100), new Skills("Soul Reaver", 60, 90, 120)});
            default: return null;
        }
    }
}