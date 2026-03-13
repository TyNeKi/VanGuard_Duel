package GUIBattle;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import Models.Characters;
import Models.Skills;
import Data.CharacterRegistry;
import Logic.BattleLogic;

public class GUIBattleScreen extends JFrame {
    final private Characters player, enemy;
    private JLabel pStats, eStats, pSprite, eSprite;
    private JPanel actionPanel;
    private boolean isBusy = false;
    final  private Random rand = new Random();

    public GUIBattleScreen(Characters selected) {
        this.player = selected;
        this.enemy = generateAI();
        initUI();
    }

    private void initUI() {
        // Specify the system boundary [cite: 191, 210]
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        // --- STATS HEADER ---
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 150));

        pStats = new JLabel();
        eStats = new JLabel("", SwingConstants.RIGHT);
        pStats.setFont(new Font("Monospaced", Font.BOLD, 26));
        eStats.setFont(new Font("Monospaced", Font.BOLD, 26));

        header.add(pStats);
        header.add(eStats);
        add(header, BorderLayout.NORTH);

        // --- ARENA ---
        // --- ARENA (REWRITTEN FOR SYMMETRY) ---
// GridBagLayout allows us to position them precisely in the center-ish area
        JPanel arena = new JPanel(new GridBagLayout());
        arena.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        pSprite = new JLabel("", SwingConstants.CENTER);
        eSprite = new FlippedLabel(); // Keep our flipped logic for the enemy

// Player Sprite Positioning
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH; // Stand on the "floor"
        gbc.insets = new Insets(0, 250, 0, 0); // Pushes Tyron 250px from the left edge
        arena.add(pSprite, gbc);

// Enemy Sprite Positioning
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 0, 250); // Pushes Lance 250px from the right edge
        arena.add(eSprite, gbc);

        add(arena, BorderLayout.CENTER);
        // --- ACTION FOOTER ---
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setPreferredSize(new Dimension(0, 200));
        setupButtons();
        add(actionPanel, BorderLayout.SOUTH);

        refreshStats();
        loadGif(pSprite, player.getName(), "idle");
        loadGif(eSprite, enemy.getName(), "idle");
    }

    private void setupButtons() {
        actionPanel.removeAll();
        for (Skills s : player.getSkills()) {
            JButton b = new JButton("<html><center><b>" + s.getSkillName() + "</b><br>"
                    + s.getManaCost() + " Mana</center></html>");
            b.setPreferredSize(new Dimension(280, 130));
            b.setBackground(Color.WHITE); // White background
            b.setForeground(Color.BLACK); // FIX: Black font color
            b.setFocusPainted(false);
            b.addActionListener(e -> playerTurn(s));
            actionPanel.add(b);
        }

        // --- REST BUTTON ---
        JButton rest = new JButton("<html><center><b>REST</b><br>Regen Mana</center></html>");
        rest.setPreferredSize(new Dimension(200, 130));
        rest.setBackground(new Color(100, 70, 20));
        rest.setForeground(Color.BLACK); // FIX: Black font color
        rest.addActionListener(e -> {
            if (isBusy) return;
            player.updateMana(player.getManaPerTurn() * 2);
            refreshStats();
            computerTurn();
        });
        actionPanel.add(rest);
    }

    private void playerTurn(Skills s) {
        if (isBusy || player.getMana() < s.getManaCost()) return;
        isBusy = true;
        toggleButtons(false);

        // Determine skill animation index
        int idx = 1;
        for (int i = 0; i < player.getSkills().length; i++) {
            if (player.getSkills()[i] == s) { idx = i + 1; break; }
        }

        loadGif(pSprite, player.getName(), "skill" + idx);

        new Timer(1200, e -> {
            player.updateMana(-s.getManaCost());
            int dmg = BattleLogic.calculateDamage(player, enemy, s);
            BattleLogic.processEffects(s, player, enemy, dmg);

            loadGif(eSprite, enemy.getName(), "gothit");
            refreshStats();

            new Timer(800, e2 -> {
                loadGif(pSprite, player.getName(), "idle");
                if (enemy.getHp() <= 0) finalizeDuel("VICTORY", eSprite, enemy.getName());
                else {
                    loadGif(eSprite, enemy.getName(), "idle");
                    computerTurn();
                }
                ((Timer)e2.getSource()).stop();
            }).start();
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void computerTurn() {
        enemy.updateMana(enemy.getManaPerTurn());
        int pick = rand.nextInt(3);
        Skills s = enemy.getSkills()[pick];

        if (enemy.getMana() >= s.getManaCost()) {
            isBusy = true;
            loadGif(eSprite, enemy.getName(), "skill" + (pick + 1));

            new Timer(1200, e -> {
                enemy.updateMana(-s.getManaCost());
                int dmg = BattleLogic.calculateDamage(enemy, player, s);
                BattleLogic.processEffects(s, enemy, player, dmg);

                loadGif(pSprite, player.getName(), "gothit");
                refreshStats();

                new Timer(800, e2 -> {
                    loadGif(eSprite, enemy.getName(), "idle");
                    if (player.getHp() <= 0) finalizeDuel("DEFEAT", pSprite, player.getName());
                    else {
                        loadGif(pSprite, player.getName(), "idle");
                        player.updateMana(player.getManaPerTurn());
                        refreshStats();
                        isBusy = false;
                        toggleButtons(true);
                    }
                    ((Timer)e2.getSource()).stop();
                }).start();
                ((Timer)e.getSource()).stop();
            }).start();
        } else {
            // AI rests if low on mana
            enemy.updateMana(enemy.getManaPerTurn() * 2);
            player.updateMana(player.getManaPerTurn());
            refreshStats();
            isBusy = false;
            toggleButtons(true);
        }
    }

    private void finalizeDuel(String msg, JLabel sprite, String name) {
        loadGif(sprite, name, "defeat");
        new Timer(2000, e -> {
            JOptionPane.showMessageDialog(this, msg);
            dispose();
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void refreshStats() {
        pStats.setText(String.format("<html><font color='white'>&nbsp;%s<br><font color='#FF3333'>HP: %d/%d</font><br><font color='#33FFFF'>MANA: %d/%d</font></font></html>",
                player.getName(), player.getHp(), player.getMaxHp(), player.getMana(), player.getMaxMana()));

        eStats.setText(String.format("<html><font color='white'>%s&nbsp;<br><font color='#FF3333'>HP: %d/%d</font>&nbsp;<br><font color='#33FFFF'>MANA: %d/%d</font>&nbsp;</font></html>",
                enemy.getName(), enemy.getHp(), enemy.getMaxHp(), enemy.getMana(), enemy.getMaxMana()));
    }

    private void loadGif(JLabel l, String n, String a) {
        java.net.URL url = getClass().getResource("/resources/" + n + "_" + a + ".gif");
        if (url != null) {
            l.setIcon(new ImageIcon(url));
            l.setText("");
        } else {
            l.setText("<html><font color='white'>" + n + " (" + a + ")</font></html>");
        }
    }

    private void toggleButtons(boolean state) {
        for (Component c : actionPanel.getComponents()) c.setEnabled(state);
    }

    private Characters generateAI() {
        String[] ns = CharacterRegistry.getAllNames();
        String n;
        do {
            n = ns[rand.nextInt(ns.length)];
        } while (n.equals(player.getName()));
        return CharacterRegistry.getCharacter(n);
    }


}