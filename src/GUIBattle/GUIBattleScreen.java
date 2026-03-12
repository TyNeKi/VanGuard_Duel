package GUIBattle;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import Models.Characters;
import Models.Skills;

public class GUIBattleScreen extends JFrame {
    private Characters player, computer;
    private JLabel pStats, cStats;
    private Random rand = new Random();

    public GUIBattleScreen(Characters selected) {
        this.player = selected;
        this.computer = generateRandomEnemy(selected.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setupTopStats();
        setupActionPanel();
    }

    private void setupTopStats() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        statsPanel.setBackground(Color.BLACK);
        pStats = new JLabel(formatStats(player));
        pStats.setFont(new Font("Arial", Font.BOLD, 22));
        cStats = new JLabel(formatStats(computer), SwingConstants.RIGHT);
        cStats.setFont(new Font("Arial", Font.BOLD, 22));
        statsPanel.add(pStats);
        statsPanel.add(cStats);
        add(statsPanel, BorderLayout.NORTH);
    }

    private void setupActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(Color.DARK_GRAY);
        for (Skills skill : player.getSkills()) {
            JButton btn = new JButton("<html>" + skill.getSkillName() + "<br>" + skill.getManaCost() + " MP</html>");
            btn.setPreferredSize(new Dimension(200, 80));
            btn.addActionListener(e -> playerTurn(skill));
            actionPanel.add(btn);
        }
        JButton restBtn = new JButton("REST");
        restBtn.setPreferredSize(new Dimension(200, 80));
        restBtn.addActionListener(e -> restAction());
        actionPanel.add(restBtn);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void playerTurn(Skills skill) {
        if (player.getMana() >= skill.getManaCost()) {
            player.setMana(player.getMana() - skill.getManaCost());
            int damage = rand.nextInt((skill.getMaxDamage() - skill.getMinDamage()) + 1) + skill.getMinDamage();
            computer.setHp(Math.max(0, computer.getHp() - damage));
            updateUI();
            JOptionPane.showMessageDialog(this, player.getName() + " hits for " + damage + "!");
            if (checkGameOver()) return;
            computerTurn();
        } else {
            JOptionPane.showMessageDialog(this, "Not enough Mana!");
        }
    }

    private void restAction() {
        player.setMana(Math.min(player.getMaxMana(), player.getMana() + (player.getManaPerTurn() * 2)));
        updateUI();
        JOptionPane.showMessageDialog(this, "Resting... Mana restored!");
        computerTurn();
    }

    private void computerTurn() {
        computer.setMana(Math.min(computer.getMaxMana(), computer.getMana() + computer.getManaPerTurn()));
        Skills s = computer.getSkills()[rand.nextInt(3)];
        int d = rand.nextInt((s.getMaxDamage() - s.getMinDamage()) + 1) + s.getMinDamage();
        player.setHp(Math.max(0, player.getHp() - d));
        updateUI();
        JOptionPane.showMessageDialog(this, computer.getName() + " attacks for " + d + "!");
        if (checkGameOver()) return;
        player.setMana(Math.min(player.getMaxMana(), player.getMana() + player.getManaPerTurn()));
        updateUI();
    }

    private boolean checkGameOver() {
        if (computer.getHp() <= 0) { JOptionPane.showMessageDialog(this, "Victory!"); dispose(); return true; }
        if (player.getHp() <= 0) { JOptionPane.showMessageDialog(this, "Defeat!"); dispose(); return true; }
        return false;
    }

    private void updateUI() {
        pStats.setText(formatStats(player));
        cStats.setText(formatStats(computer));
    }

    private String formatStats(Characters c) {
        return "<html><font color='white'>" + c.getName() + "<br>HP: " + c.getHp() + "<br>MP: " + c.getMana() + "</font></html>";
    }

    private Characters generateRandomEnemy(String pName) {
        String[] ns = {"Tyron", "Lance", "Adrian", "Clark", "Raze", "Marie", "Alyana", "Katarina"};
        String eN; do { eN = ns[rand.nextInt(ns.length)]; } while (eN.equals(pName));
        return getEnemyStats(eN);
    }

    private Characters getEnemyStats(String n) {
        switch (n) {
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