package gameengine;

import javax.swing.*;

import gamemodel.Characters;
import gamemodel.Skills;

import java.awt.*;
import java.util.Random;

import logic.BattleLogic;

public class GUIBattleScreen extends JFrame {
    private Characters player, enemy;
    private JLabel pStats, eStats, pSprite, eSprite;
    private JPanel actionPanel, arena;
    private boolean isBusy = false;
    private final Random rand = new Random();
    private boolean isPvP = false;
    private boolean isPlayerOneTurn = true;

    public GUIBattleScreen(Characters selected) {
        this.player = selected;
        this.enemy = generateAI();
        this.isPvP = false;
        initUI();
    }

    public GUIBattleScreen(Characters player1, Characters player2) {
        this.player = player1;
        this.enemy = player2;
        this.isPvP = true;
        initUI();
    }

    private void initUI() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        // --- 1. HEADER (Stats) ---
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 150));
        pStats = new JLabel();
        eStats = new JLabel("", SwingConstants.RIGHT);
        pStats.setFont(new Font("Monospaced", Font.BOLD, 26));
        eStats.setFont(new Font("Monospaced", Font.BOLD, 26));
        header.add(pStats); header.add(eStats);
        add(header, BorderLayout.NORTH);

        // --- 2. ARENA (Characters) ---
        // Setting layout to null allows free movement and fixes the floating issue
        arena = new JPanel(null);
        arena.setOpaque(false);

        pSprite = new JLabel("", SwingConstants.CENTER);
        eSprite = new FlippedLabel(true); // Ensures the enemy faces you

        // MANUAL POSITIONING (The "Floor")
        // x, y, width, height. We keep y=350 so they are on the same line.
        pSprite.setBounds(300, 350, 300, 300);
        eSprite.setBounds(1250, 350, 300, 300);

        arena.add(pSprite);
        arena.add(eSprite);
        add(arena, BorderLayout.CENTER);

        // --- 3. FOOTER (Actions) ---
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setPreferredSize(new Dimension(0, 200));
        setupButtons();
        add(actionPanel, BorderLayout.SOUTH);

        refresh();
        loadGif(pSprite, player.getName(), "idle");
        loadGif(eSprite, enemy.getName(), "idle");
    }

    private void setupButtons() {
        actionPanel.removeAll();
        for (Skills s : player.getSkills()) {
            JButton b = new JButton("<html><center><b>" + s.getSkillName() + "</b><br>"
                    + "<font color='blue'>" + s.getManaCost() + " Mana</font></center></html>");
            b.setPreferredSize(new Dimension(280, 130));
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
            b.setFocusPainted(false);
            b.addActionListener(e -> playerTurn(s));
            actionPanel.add(b);
        }

        JButton rest = new JButton("<html><center><b>REST</b><br><font color='orange'>Regen Mana</font></center></html>");
        rest.setPreferredSize(new Dimension(200, 130));
        rest.setBackground(new Color(80, 60, 20));
        rest.setForeground(Color.BLACK);
        rest.addActionListener(e -> {
            if (!isBusy) {
                player.updateMana(player.getManaPerTurn() * 2);
                refresh();
                computerTurn();
            }
        });
        actionPanel.add(rest);
    }

    private void playerTurn(Skills s) {
        Characters attacker = isPvP
                ? (isPlayerOneTurn ? player : enemy)
                : player;

        Characters defender = isPvP
                ? (isPlayerOneTurn ? enemy : player)
                : enemy;

        if (isBusy || attacker.getMana() < s.getManaCost()) return;

        isBusy = true;
        toggleButtons(false);

        attacker.updateMana(-s.getManaCost());
        int dmg = BattleLogic.calculateDamage(attacker, defender, s);
        BattleLogic.processEffects(s, attacker, defender, dmg);

        refresh();

        if (defender.getHp() <= 0) {
            String result = isPvP
                    ? (isPlayerOneTurn ? "PLAYER 1 WINS" : "PLAYER 2 WINS")
                    : "VICTORY";

            finalizeDuel(result,
                    isPlayerOneTurn ? eSprite : pSprite,
                    defender.getName());
            return;
        }

        // IF PvP → SWITCH TURN
        if (isPvP) {
            isPlayerOneTurn = !isPlayerOneTurn;
            updateButtonsForCurrentPlayer();
            isBusy = false;
            toggleButtons(true);
        }
        // IF PvE → COMPUTER TURN
        else {
            computerTurn();
        }
    }

    private void updateButtonsForCurrentPlayer() {
        actionPanel.removeAll();

        Characters current = isPlayerOneTurn ? player : enemy;

        for (Skills s : current.getSkills()) {
            JButton b = new JButton("<html><center><b>" + s.getSkillName() + "</b><br>"
                    + s.getManaCost() + " Mana</center></html>");

            b.setPreferredSize(new Dimension(280, 130));
            b.addActionListener(e -> playerTurn(s));

            actionPanel.add(b);
        }

        JButton rest = new JButton("<html><center><b>REST</b><br><font color='orange'>Regen Mana</font></center></html>");
        rest.setPreferredSize(new Dimension(200, 130));
        rest.setBackground(new Color(80, 60, 20));

        rest.addActionListener(e -> {
            if (!isBusy) {
                Characters currentPlayer = isPlayerOneTurn ? player : enemy;

                currentPlayer.updateMana(currentPlayer.getManaPerTurn() * 2);
                refresh();

                // switch turn after rest
                isPlayerOneTurn = !isPlayerOneTurn;
                updateButtonsForCurrentPlayer();
            }
        });

        actionPanel.add(rest);

        revalidate();
        repaint();
    }

    private void computerTurn() {
        enemy.updateMana(enemy.getManaPerTurn());
        int pick = rand.nextInt(3);
        Skills s = enemy.getSkills()[pick];

        if (enemy.getMana() < s.getManaCost()) {
            enemy.updateMana(enemy.getManaPerTurn() * 2);
            refresh(); isBusy = false; toggleButtons(true);
            return;
        }

        Point home = eSprite.getLocation();
        Point target = new Point(pSprite.getLocation().x + 100, home.y);

        moveSprite(eSprite, enemy.getName(), target, () -> {
            loadGif(eSprite, enemy.getName(), "skill" + (pick + 1));
            new Timer(1200, e1 -> {
                enemy.updateMana(-s.getManaCost());
                int dmg = BattleLogic.calculateDamage(enemy, player, s);
                BattleLogic.processEffects(s, enemy, player, dmg);
                loadGif(pSprite, player.getName(), "gothit");
                refresh();
                new Timer(800, e2 -> {
                    moveSprite(eSprite, enemy.getName(), home, () -> {
                        loadGif(eSprite, enemy.getName(), "idle");
                        if (player.getHp() <= 0) finalizeDuel("DEFEAT", pSprite, player.getName());
                        else { loadGif(pSprite, player.getName(), "idle"); player.updateMana(player.getManaPerTurn()); refresh(); isBusy = false; toggleButtons(true); }
                    });
                    ((Timer)e2.getSource()).stop();
                }).start();
                ((Timer)e1.getSource()).stop();
            }).start();
        });
    }

    private void moveSprite(JLabel sprite, String name, Point dest, Runnable onDone) {
        loadGif(sprite, name, "walk");
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            Point cur = sprite.getLocation();
            int speed = 12;
            int nextX = cur.x;

            if (cur.x < dest.x) nextX = Math.min(cur.x + speed, dest.x);
            else if (cur.x > dest.x) nextX = Math.max(cur.x - speed, dest.x);

            // Stay on the same Y-level (no floating!)
            sprite.setLocation(nextX, cur.y);

            if (sprite.getLocation().x == dest.x) {
                ((Timer)e.getSource()).stop();
                onDone.run();
            }
        });
        t.start();
    }

    private void refresh() {
        pStats.setText(String.format("<html><font color='white'>&nbsp;%s<br><font color='#FF4444'>HP: %d/%d</font><br><font color='#44FFFF'>MANA: %d/%d</font></font></html>",
                player.getName(), player.getHp(), player.getMaxHp(), player.getMana(), player.getMaxMana()));
        eStats.setText(String.format("<html><font color='white'>%s&nbsp;<br><font color='#FF4444'>HP: %d/%d</font>&nbsp;<br><font color='#44FFFF'>MANA: %d/%d</font>&nbsp;</font></html>",
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

    private void toggleButtons(boolean s) { for (Component c : actionPanel.getComponents()) c.setEnabled(s); }
    private int getSkillIndex(Skills s) { for (int i=0; i<3; i++) if (player.getSkills()[i] == s) return i+1; return 1; }
    private void finalizeDuel(String m, JLabel s, String n) { loadGif(s, n, "defeat"); new Timer(2000, e -> { JOptionPane.showMessageDialog(this, m); dispose(); ((Timer)e.getSource()).stop(); }).start(); }
    private Characters generateAI() { String[] ns = CharacterRegistry.getAllNames(); String n; do { n = ns[rand.nextInt(ns.length)]; } while (n.equals(player.getName())); return CharacterRegistry.getCharacter(n); }

    class FlippedLabel extends JLabel {
        private final boolean flip;
        public FlippedLabel(boolean flip) { this.flip = flip; }
        @Override
        protected void paintComponent(Graphics g) {
            if (flip) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(getWidth(), 0);
                g2.scale(-1, 1);
                super.paintComponent(g2);
                g2.dispose();
            } else { super.paintComponent(g); }
        }
    }
}