package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import gamemodel.Skills;
import java.awt.*;
import java.util.Random;
import java.net.URL;
import Logic.BattleLogic;

public class GUIBattleScreen extends JFrame {
    private Characters player, enemy;
    private JLabel pSprite, eSprite, pRoundsLabel, eRoundsLabel;
    private JProgressBar pHP, pMana, eHP, eMana;
    private JPanel actionPanel, arena;
    private boolean isBusy = false;
    private boolean isPvP = false;
    private int pRoundsWon = 0, eRoundsWon = 0;
    private final Random rand = new Random();

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
        setTitle("VanGuard Duel - Arena");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        // HUD
        JPanel hud = new JPanel(new GridLayout(1, 3, 20, 0));
        hud.setBackground(Color.BLACK);
        hud.setPreferredSize(new Dimension(0, 150));
        hud.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JPanel roundTracker = new JPanel(new GridLayout(2, 1));
        roundTracker.setOpaque(false);
        pRoundsLabel = new JLabel("PLAYER 1 WINS: 0", SwingConstants.CENTER);
        eRoundsLabel = new JLabel(isPvP ? "PLAYER 2 WINS: 0" : "ENEMY WINS: 0", SwingConstants.CENTER);
        pRoundsLabel.setForeground(Color.YELLOW); eRoundsLabel.setForeground(Color.YELLOW);
        pRoundsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        eRoundsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roundTracker.add(pRoundsLabel); roundTracker.add(eRoundsLabel);

        hud.add(createStatPanel(player.getName(), pHP = new JProgressBar(), pMana = new JProgressBar()));
        hud.add(roundTracker);
        hud.add(createStatPanel(enemy.getName(), eHP = new JProgressBar(), eMana = new JProgressBar()));
        add(hud, BorderLayout.NORTH);

        // Arena (Balanced Position)
        arena = new JPanel(null);
        arena.setOpaque(false);
        pSprite = new JLabel();
        eSprite = new FlippedLabel(true);

        pSprite.setBounds(350, 450, 300, 300);
        eSprite.setBounds(1250, 450, 300, 300);

        arena.add(pSprite); arena.add(eSprite);
        add(arena, BorderLayout.CENTER);

        // Footer
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setPreferredSize(new Dimension(0, 200));
        setupButtons();
        add(actionPanel, BorderLayout.SOUTH);

        refresh();
        loadGif(pSprite, player.getName(), "idle");
        loadGif(eSprite, enemy.getName(), "idle");
    }

    private void playerTurn(Skills s) {
        if (isBusy || player.getMana() < s.getManaCost()) return;
        isBusy = true; toggleButtons(false);

        // Identify skill for GIF
        int skillIdx = 1;
        for (int i = 0; i < player.getSkills().length; i++) {
            if (player.getSkills()[i].getSkillName().equals(s.getSkillName())) {
                skillIdx = i + 1; break;
            }
        }
        final String skillAction = "skill" + skillIdx;

        Point origin = pSprite.getLocation();
        // Stop exactly 100 pixels in front of the enemy
        Point target = new Point(eSprite.getX() - 150, origin.y);

        moveSprite(pSprite, player.getName(), target, () -> {
            loadGif(pSprite, player.getName(), skillAction); // Play specific skill
            player.updateMana(-s.getManaCost());
            BattleLogic.processEffects(s, player, enemy, BattleLogic.calculateDamage(player, enemy, s));
            refresh();

            new Timer(1200, e -> {
                loadGif(eSprite, enemy.getName(), "gothit");
                moveSprite(pSprite, player.getName(), origin, () -> {
                    loadGif(pSprite, player.getName(), "idle");
                    checkRoundOver();
                });
                ((Timer)e.getSource()).stop();
            }).start();
        });
    }

    private void computerTurn() {
        if (isPvP) { isBusy = false; toggleButtons(true); return; }

        isBusy = true; toggleButtons(false);
        new Timer(1000, e -> {
            enemy.updateMana(enemy.getManaPerTurn());
            int pick = rand.nextInt(3);
            Skills s = enemy.getSkills()[pick];

            if (enemy.getMana() >= s.getManaCost()) {
                String skillAction = "skill" + (pick + 1);
                Point origin = eSprite.getLocation();
                // Move in front of player
                Point target = new Point(pSprite.getX() + 150, origin.y);

                moveSprite(eSprite, enemy.getName(), target, () -> {
                    loadGif(eSprite, enemy.getName(), skillAction);
                    enemy.updateMana(-s.getManaCost());
                    BattleLogic.processEffects(s, enemy, player, BattleLogic.calculateDamage(enemy, player, s));
                    refresh();

                    new Timer(1200, e2 -> {
                        loadGif(pSprite, player.getName(), "gothit");
                        moveSprite(eSprite, enemy.getName(), origin, () -> {
                            loadGif(eSprite, enemy.getName(), "idle");
                            loadGif(pSprite, player.getName(), "idle");
                            isBusy = false; toggleButtons(true); // Control back to player
                        });
                        ((Timer)e2.getSource()).stop();
                    }).start();
                });
            } else {
                enemy.updateMana(enemy.getManaPerTurn() * 3); refresh();
                isBusy = false; toggleButtons(true);
            }
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void moveSprite(JLabel sprite, String name, Point dest, Runnable onDone) {
        loadGif(sprite, name, "walk");
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            int speed = 25;
            int curX = sprite.getX();
            int nextX = (curX < dest.x) ? Math.min(curX + speed, dest.x) : Math.max(curX - speed, dest.x);
            sprite.setLocation(nextX, sprite.getY());
            if (sprite.getX() == dest.x) { ((Timer)e.getSource()).stop(); onDone.run(); }
        });
        t.start();
    }

    private void checkRoundOver() {
        if (enemy.getHp() <= 0) {
            pRoundsWon++; pRoundsLabel.setText("PLAYER 1 WINS: " + pRoundsWon);
            if (pRoundsWon >= 2) finalizeMatch(player.getName() + " WINS!");
            else resetRound();
        } else if (player.getHp() <= 0) {
            eRoundsWon++; eRoundsLabel.setText("ENEMY WINS: " + eRoundsWon);
            if (eRoundsWon >= 2) finalizeMatch(enemy.getName() + " WINS!");
            else resetRound();
        } else {
            // IF NO ONE DIED AND IT WAS PLAYER'S TURN: Trigger Computer
            if (!isPvP && isBusy) computerTurn();
            else { isBusy = false; toggleButtons(true); }
        }
    }

    private void setupButtons() {
        actionPanel.removeAll();
        for (Skills s : player.getSkills()) {
            JButton b = new JButton("<html><center><b>" + s.getSkillName() + "</b><br>" + s.getManaCost() + " MP</center></html>");
            b.setPreferredSize(new Dimension(220, 120));
            b.addActionListener(e -> playerTurn(s));
            actionPanel.add(b);
        }
        JButton rest = new JButton("REST");
        rest.setPreferredSize(new Dimension(150, 120));
        rest.setBackground(new Color(0, 100, 0)); rest.setForeground(Color.WHITE);
        rest.addActionListener(e -> {
            if (!isBusy) {
                player.updateMana(player.getManaPerTurn() * 4); refresh();
                computerTurn();
            }
        });
        actionPanel.add(rest);

        JButton exit = new JButton("EXIT");
        exit.setPreferredSize(new Dimension(150, 120));
        exit.setBackground(new Color(150, 0, 0)); exit.setForeground(Color.WHITE);
        exit.addActionListener(e -> { new GUIStartScreen().setVisible(true); dispose(); });
        actionPanel.add(exit);
        actionPanel.revalidate(); actionPanel.repaint();
    }

    private void resetRound() {
        JOptionPane.showMessageDialog(this, "Next Round!");
        player.updateHp(player.getMaxHp()); player.updateMana(player.getMaxMana());
        enemy.updateHp(enemy.getMaxHp()); enemy.updateMana(enemy.getMaxMana());
        refresh(); isBusy = false; toggleButtons(true);
    }

    private void finalizeMatch(String msg) {
        JOptionPane.showMessageDialog(this, msg);
        new GUIStartScreen().setVisible(true); dispose();
    }

    private JPanel createStatPanel(String name, JProgressBar hp, JProgressBar mana) {
        JPanel p = new JPanel(new GridLayout(3, 1)); p.setOpaque(false);
        JLabel l = new JLabel(name.toUpperCase(), SwingConstants.CENTER);
        l.setForeground(Color.WHITE); l.setFont(new Font("Impact", Font.PLAIN, 24));
        hp.setForeground(Color.RED); hp.setStringPainted(true);
        mana.setForeground(Color.CYAN); mana.setStringPainted(true);
        p.add(l); p.add(hp); p.add(mana);
        return p;
    }

    private void refresh() {
        pHP.setMaximum(player.getMaxHp()); pHP.setValue(player.getHp());
        pMana.setMaximum(player.getMaxMana()); pMana.setValue(player.getMana());
        eHP.setMaximum(enemy.getMaxHp()); eHP.setValue(enemy.getHp());
        eMana.setMaximum(enemy.getMaxMana()); eMana.setValue(enemy.getMana());
    }

    private void loadGif(JLabel l, String n, String a) {
        URL url = getClass().getResource("/resources/" + n + "_" + a + ".gif");
        if (url != null) l.setIcon(new ImageIcon(url));
    }

    private void toggleButtons(boolean s) { for (Component c : actionPanel.getComponents()) c.setEnabled(s); }

    private Characters generateAI() {
        String[] ns = CharacterRegistry.getAllNames();
        return CharacterRegistry.getCharacter(ns[rand.nextInt(ns.length)]);
    }
}