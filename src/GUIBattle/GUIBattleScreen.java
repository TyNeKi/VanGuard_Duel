package GUIBattle;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import Models.Characters;
import Models.Skills;
import Data.CharacterRegistry;
import Logic.BattleLogic;

public class GUIBattleScreen extends JFrame {
    private Characters player, computer;
    private JLabel pStats, cStats, pSprite, cSprite;
    private JPanel actionPanel;
    private Random rand = new Random();
    private boolean isAnimating = false;

    public GUIBattleScreen(Characters selected) {
        this.player = selected;
        this.computer = generateAI();
        initUI();
    }

    private void initUI() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 150));
        pStats = new JLabel();
        cStats = new JLabel("", SwingConstants.RIGHT);
        pStats.setFont(new Font("Monospaced", Font.BOLD, 26));
        cStats.setFont(new Font("Monospaced", Font.BOLD, 26));
        header.add(pStats); header.add(cStats);
        add(header, BorderLayout.NORTH);

        JPanel arena = new JPanel(new GridLayout(1, 2));
        arena.setOpaque(false);
        pSprite = new JLabel("", SwingConstants.CENTER);
        cSprite = new JLabel("", SwingConstants.CENTER);
        pSprite.setVerticalAlignment(SwingConstants.BOTTOM);
        cSprite.setVerticalAlignment(SwingConstants.BOTTOM);
        arena.add(pSprite); arena.add(cSprite);
        add(arena, BorderLayout.CENTER);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setPreferredSize(new Dimension(0, 200));
        setupButtons();
        add(actionPanel, BorderLayout.SOUTH);

        refresh();
        setGIF(pSprite, player.getName(), "idle");
        setGIF(cSprite, computer.getName(), "idle");
    }

    private void setupButtons() {
        actionPanel.removeAll();
        for (Skills s : player.getSkills()) {
            JButton b = new JButton("<html><center><font color='white'><b>" + s.getSkillName() + "</b></font><br>"
                    + "<font color='#00FFFF'>" + s.getManaCost() + " MANA</font><br>"
                    + "<font size='3' color='#BBBBBB'>" + getSkillDesc(s.getSkillName()) + "</font></center></html>");
            b.setPreferredSize(new Dimension(280, 130));
            b.setBackground(new Color(55, 55, 55));
            b.addActionListener(e -> playerTurn(s));
            actionPanel.add(b);
        }
        JButton rest = new JButton("<html><center><font color='white'><b>REST</b></font><br><font color='#FFCC00'>Gain Extra Mana</font></center></html>");
        rest.setPreferredSize(new Dimension(200, 130));
        rest.setBackground(new Color(100, 70, 20));
        rest.addActionListener(e -> { if (!isAnimating) { player.setMana(player.getMana() + (player.getManaPerTurn() * 2)); refresh(); computerTurn(); } });
        actionPanel.add(rest);
    }

    private void playerTurn(Skills s) {
        if (isAnimating || player.getMana() < s.getManaCost()) return;
        isAnimating = true; toggleButtons(false);
        int idx = 1;
        for (int i = 0; i < player.getSkills().length; i++) if (player.getSkills()[i] == s) idx = i + 1;
        setGIF(pSprite, player.getName(), "skill" + idx);

        new Timer(1200, e -> {
            player.setMana(player.getMana() - s.getManaCost());
            int dmg = BattleLogic.calculateDamage(player, computer, s);
            BattleLogic.processEffects(s, player, computer, dmg);
            setGIF(cSprite, computer.getName(), "gothit");
            refresh();
            new Timer(800, e2 -> {
                setGIF(pSprite, player.getName(), "idle");
                if (computer.getHp() <= 0) finalizeDuel("VICTORY", cSprite, computer.getName());
                else { setGIF(cSprite, computer.getName(), "idle"); computerTurn(); }
                ((Timer)e2.getSource()).stop();
            }).start();
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void computerTurn() {
        computer.setMana(computer.getMana() + computer.getManaPerTurn());
        refresh();
        int idx = rand.nextInt(3); Skills s = computer.getSkills()[idx];
        if (computer.getMana() >= s.getManaCost()) {
            isAnimating = true;
            setGIF(cSprite, computer.getName(), "skill" + (idx + 1));
            new Timer(1200, e -> {
                computer.setMana(computer.getMana() - s.getManaCost());
                int dmg = BattleLogic.calculateDamage(computer, player, s);
                BattleLogic.processEffects(s, computer, player, dmg);
                setGIF(pSprite, player.getName(), "gothit");
                refresh();
                new Timer(800, e2 -> {
                    setGIF(cSprite, computer.getName(), "idle");
                    if (player.getHp() <= 0) finalizeDuel("DEFEAT", pSprite, player.getName());
                    else { setGIF(pSprite, player.getName(), "idle"); player.setMana(player.getMana() + player.getManaPerTurn()); refresh(); isAnimating = false; toggleButtons(true); }
                    ((Timer)e2.getSource()).stop();
                }).start();
                ((Timer)e.getSource()).stop();
            }).start();
        } else { isAnimating = false; toggleButtons(true); }
    }

    private void finalizeDuel(String msg, JLabel sprite, String name) {
        setGIF(sprite, name, "defeat");
        new Timer(2000, e -> { JOptionPane.showMessageDialog(this, msg); dispose(); ((Timer)e.getSource()).stop(); }).start();
    }

    private void setGIF(JLabel l, String n, String a) {
        java.net.URL url = getClass().getResource("/resources/" + n + "_" + a + ".gif");
        if (url != null) { l.setIcon(new ImageIcon(url)); l.setText(""); }
        else { l.setIcon(null); l.setText("<html><font color='white'>" + n + " (" + a + ")</font></html>"); }
    }

    private void refresh() {
        pStats.setText(String.format("<html><font color='white'>&nbsp;%s<br><font color='#FF4444'>HP: %d/%d</font><br><font color='#44FFFF'>MANA: %d/%d</font></font></html>", player.getName(), player.getHp(), player.getMaxHp(), player.getMana(), player.getMaxMana()));
        cStats.setText(String.format("<html><font color='white'>%s&nbsp;<br><font color='#FF4444'>HP: %d/%d</font>&nbsp;<br><font color='#44FFFF'>MANA: %d/%d</font>&nbsp;</font></html>", computer.getName(), computer.getHp(), computer.getMaxHp(), computer.getMana(), computer.getMaxMana()));
    }

    private void toggleButtons(boolean s) { for (Component c : actionPanel.getComponents()) c.setEnabled(s); }
    private String getSkillDesc(String n) { switch(n){ case "Energy Guard": return "Shield -45%"; case "Light Pillar": return "Blind -40%"; case "Radiant Blessing": return "Heal HP"; case "Mana Burn": return "Burn 20 Mana"; case "Soul Reaver": return "30% Lifesteal"; case "Divine Retribution": return "25% Lifesteal"; default: return "Balanced DMG"; } }
    private Characters generateAI() { String[] ns = CharacterRegistry.getAllNames(); String n; do { n = ns[rand.nextInt(ns.length)]; } while (n.equals(player.getName())); return CharacterRegistry.getCharacter(n); }
}