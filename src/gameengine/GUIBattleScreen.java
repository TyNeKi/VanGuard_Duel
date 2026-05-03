package gameengine;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import gamemodel.Characters;
import gamemodel.Skills;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;
import java.net.URL;
import Logic.BattleLogic;

public class GUIBattleScreen extends JPanel {
    private Characters player1, player2, currentPlayer, opponent;
    private JLabel pSprite, eSprite;
    private JProgressBar pHP, pMana, eHP, eMana;
    private JPanel actionPanel, arena, hud, statContainer;
    private JTextPane battleLog;
    private Timer turnTimer;
    private boolean isBusy = false;
    private boolean isPvP = false;
    private boolean isArcade = false;
    private int p1RoundsWon = 0, p2RoundsWon = 0;
    private final Random rand = new Random();
    private JLabel turnTimerLabel;
    private Timer displayTimer;
    private int remainingTime;
    private String[] arcadeOpponents;
    private int currentArcadeIndex = 0;
    private int arcadeDefeats = 0;
    private JLabel currentTurnLabel;
    private ActionListener onExitListener;
    private Clip currentSkillSound;
    private String map;

    public GUIBattleScreen(Characters selected, String map) {
        this.player1 = selected;
        this.currentPlayer = selected;
        this.opponent = generateAI();
        this.isPvP = false;
        this.isArcade = false;
        this.map = map;
        initUI();
    }

    public GUIBattleScreen(Characters player1, Characters player2, String map) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.opponent = player2;
        this.isPvP = true;
        this.isArcade = false;
        this.map = map;
        initUI();
    }

    public GUIBattleScreen(Characters selected, boolean isArcade, String map) {
        this.player1 = selected;
        this.currentPlayer = selected;
        this.isPvP = false;
        this.isArcade = isArcade;
        this.arcadeOpponents = buildArcadeOpponents(selected.getName());
        this.currentArcadeIndex = 0;
        this.arcadeDefeats = 0;
        this.map = map;
        selectNextArcadeOpponent();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        // HUD
        hud = new JPanel(new BorderLayout());
        hud.setBackground(new Color(10, 10, 50));
        hud.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2),
            BorderFactory.createEmptyBorder(10, 40, 10, 40)
        ));
        hud.setPreferredSize(new Dimension(0, 200));

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBackground(new Color(10, 10, 50));
        String initialTurnText = isPvP ? "PLAYER 1 TURN: " + player1.getName() : "Current Turn: " + currentPlayer.getName();
        currentTurnLabel = new JLabel(initialTurnText);
        currentTurnLabel.setForeground(Color.WHITE);
        currentTurnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        northPanel.add(currentTurnLabel);
        hud.add(northPanel, BorderLayout.NORTH);

        statContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        statContainer.setOpaque(false);
        String p1Label = player1.getName();
        String p2Label = isPvP ? player2.getName() : (opponent != null ? opponent.getName() : "AI");
        statContainer.add(createStatPanel(p1Label, pHP = new JProgressBar(), pMana = new JProgressBar(), p1RoundsWon, 3));
        statContainer.add(createStatPanel(p2Label, eHP = new JProgressBar(), eMana = new JProgressBar(), p2RoundsWon, 3));
        hud.add(statContainer, BorderLayout.CENTER);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerPanel.setBackground(new Color(10, 10, 50));
        turnTimerLabel = new JLabel("Time: 30");
        turnTimerLabel.setVisible(false);
        turnTimerLabel.setForeground(Color.YELLOW);
        turnTimerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerPanel.add(turnTimerLabel);
        hud.add(timerPanel, BorderLayout.SOUTH);

        add(hud, BorderLayout.NORTH);

        // Arena (Balanced Position)
        arena = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL bgURL = getClass().getResource("/resources/" + map);
                if (bgURL != null) {
                    Image img = new ImageIcon(bgURL).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        arena.setOpaque(false);
        pSprite = new JLabel();
        eSprite = new FlippedLabel(true);

        pSprite.setBounds(250, 250, 300, 300);
        eSprite.setBounds(950, 250, 300, 300);

        arena.add(pSprite); arena.add(eSprite);
        add(arena, BorderLayout.CENTER);

        // Battle Log
        battleLog = new JTextPane();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(10, 10, 40));
        battleLog.setForeground(new Color(100, 255, 100));
        battleLog.setFont(new Font("Consolas", Font.PLAIN, 12));

        final StyledDocument doc = battleLog.getStyledDocument();

        JScrollPane logScroll = new JScrollPane(battleLog);
        logScroll.setPreferredSize(new Dimension(420, 0));
        logScroll.getViewport().setBackground(new Color(10, 10, 40));
        logScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 255), 3),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 120), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            )
        ));
        
        JPanel logTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logTitlePanel.setOpaque(false);
        logTitlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        JLabel logTitle = new JLabel("⚔ BATTLE LOG ⚔");
        logTitle.setForeground(new Color(100, 200, 255));
        logTitle.setFont(new Font("Impact", Font.BOLD, 16));
        logTitlePanel.add(logTitle);
        
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setOpaque(false);
        logPanel.add(logTitlePanel, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);
        add(logPanel, BorderLayout.EAST);

        // Initialize battle log with header
        try {
            SimpleAttributeSet headerStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(headerStyle, new Color(100, 200, 255));
            StyleConstants.setBold(headerStyle, true);
            StyleConstants.setFontSize(headerStyle, 11);

            doc.insertString(0, "============================\n", headerStyle);
            doc.insertString(doc.getLength(), "BATTLE INITIATED\n", headerStyle);
            doc.insertString(doc.getLength(), "============================\n\n", headerStyle);
        } catch (Exception e) {
            // More robust logging can be added here
        }

        // Footer
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        actionPanel.setPreferredSize(new Dimension(0, 200));
        setupButtons();
        add(actionPanel, BorderLayout.SOUTH);

        refresh();
        loadGif(pSprite, player1.getName(), "idle");
        if (opponent != null) {
            loadGif(eSprite, opponent.getName(), "idle");
        }
        if (isPvP) {
            startPvPTimer();
        }
    }

    private void playerTurn(Skills s) {
        isBusy = true;
        if (turnTimer != null) turnTimer.stop();
        if (displayTimer != null) displayTimer.stop();
        toggleButtons(false);
        if (currentPlayer.getMana() < s.getManaCost()) {
            isBusy = false;
            toggleButtons(true);
            return;
        }
        
        final String attackerName = currentPlayer.getName();
        final String defenderName = opponent.getName();
        
        appendToLog(attackerName + "'s turn.");

        // Identify skill for GIF
        int skillIdx = 1;
        for (int i = 0; i < currentPlayer.getSkills().length; i++) {
            if (currentPlayer.getSkills()[i].getSkillName().equals(s.getSkillName())) {
                skillIdx = i + 1; break;
            }
        }
        final int finalSkillIdx = skillIdx;
        final String skillAction = "skill" + skillIdx;

        JLabel sprite = (isPvP && currentPlayer == player2) ? eSprite : pSprite;
        JLabel opponentSprite = (isPvP && currentPlayer == player2) ? pSprite : eSprite;

        if (isPvP) {
            // In PvP, move sprite towards opponent, attack, then move back
            Point origin = sprite.getLocation();
            Point target = (sprite == pSprite) ? new Point(eSprite.getX() - 80, origin.y) : new Point(pSprite.getX() + 80, origin.y);

            moveSprite(sprite, attackerName, target, () -> {
                if (attackerName.equals("Tyron")) {
                    currentSkillSound = UIFactory.playSound("/resources/Tyron_skill" + finalSkillIdx + "SoundEffect.wav");
                }
                loadGif(sprite, attackerName, skillAction);
                currentPlayer.updateMana(-s.getManaCost());
                int dmg = BattleLogic.calculateDamage(currentPlayer, opponent, s);
                appendToLog(attackerName + " uses " + s.getSkillName() + "!");
                BattleLogic.processEffects(s, currentPlayer, opponent, dmg);
                appendToLog("Deals " + dmg + " damage to " + defenderName + ".");
                if (s.getSkillName().equals("Radiant Blessing")) {
                    appendToLog(attackerName + " heals for 85 HP.");
                }
                if (s.getSkillName().contains("Reaver")) {
                    appendToLog(attackerName + " heals for " + (int)(dmg * 0.25) + " HP.");
                }
                if (s.getSkillName().equals("Mana Burn")) {
                    appendToLog(defenderName + " loses 20 Mana from Mana Burn.");
                }
                if (s.getSkillName().contains("Guard")) {
                    appendToLog(attackerName + " prepares to reduce damage taken by 45%.");
                }
                if (s.getSkillName().equals("Light Pillar")) {
                    appendToLog(defenderName + "'s damage dealt reduced by 40%.");
                }
                refresh();

                new Timer(1200, e -> {
                    loadGif(opponentSprite, defenderName, "gothit");
                    if (opponent.getGender().equals("female")) {
                        UIFactory.playSound("/resources/female_gothit.wav");
                    } else {
                        UIFactory.playSound("/resources/male_gothit.wav");
                    }
                    new Timer(500, eHit -> {
                        loadGif(opponentSprite, defenderName, "idle");
                        ((Timer)eHit.getSource()).stop();
                    }).start();
                    moveSprite(sprite, attackerName, origin, () -> {
                        if (currentSkillSound != null) {
                            currentSkillSound.stop();
                        }
                        loadGif(sprite, attackerName, "idle");
                        checkRoundOver();
                    });
                    ((Timer)e.getSource()).stop();
                }).start();
            });
        } else {
            // Vs AI: move sprites
            Point origin = sprite.getLocation();
            Point target = new Point(eSprite.getX() - 80, origin.y);

            moveSprite(sprite, attackerName, target, () -> {
                if (attackerName.equals("Tyron")) {
                    currentSkillSound = UIFactory.playSound("/resources/Tyron_skill" + finalSkillIdx + "SoundEffect.wav");
                }
                loadGif(sprite, attackerName, skillAction); // Play specific skill
                currentPlayer.updateMana(-s.getManaCost());
                int dmg = BattleLogic.calculateDamage(currentPlayer, opponent, s);
                appendToLog(attackerName + " uses " + s.getSkillName() + "!");
                BattleLogic.processEffects(s, currentPlayer, opponent, dmg);
                appendToLog("Deals " + dmg + " damage to " + defenderName + ".");
                if (s.getSkillName().equals("Radiant Blessing")) {
                    appendToLog(attackerName + " heals for 85 HP.");
                }
                if (s.getSkillName().contains("Reaver")) {
                    appendToLog(attackerName + " heals for " + (int)(dmg * 0.25) + " HP.");
                }
                if (s.getSkillName().equals("Mana Burn")) {
                    appendToLog(defenderName + " loses 20 Mana from Mana Burn.");
                }
                if (s.getSkillName().contains("Guard")) {
                    appendToLog(attackerName + " prepares to reduce damage taken by 45%.");
                }
                if (s.getSkillName().equals("Light Pillar")) {
                    appendToLog(defenderName + "'s damage dealt reduced by 40%.");
                }
                refresh();

                new Timer(1200, e -> {
                    loadGif(opponentSprite, defenderName, "gothit");
                    if (opponent.getGender().equals("female")) {
                        UIFactory.playSound("/resources/female_gothit.wav");
                    } else {
                        UIFactory.playSound("/resources/male_gothit.wav");
                    }
                    // Load idle after hit animation
                    new Timer(500, eHit -> {
                        loadGif(opponentSprite, defenderName, "idle");
                        ((Timer)eHit.getSource()).stop();
                    }).start();
                    moveSprite(sprite, attackerName, origin, () -> {
                        if (currentSkillSound != null) {
                            currentSkillSound.stop();
                        }
                        loadGif(sprite, attackerName, "idle");
                        checkRoundOver();
                    });
                    ((Timer)e.getSource()).stop();
                }).start();
            });
        }
    }

    private void computerTurn() {
        if (isPvP) { isBusy = false; toggleButtons(true); return; }

        if (currentTurnLabel != null) {
            currentTurnLabel.setText("Current Turn: " + opponent.getName());
        }
        isBusy = true; toggleButtons(false);
        new Timer(1000, e -> {
            opponent.updateMana(opponent.getManaPerTurn());
            int pick = rand.nextInt(3);
            Skills s = opponent.getSkills()[pick];

            if (opponent.getMana() >= s.getManaCost()) {
                appendToLog(opponent.getName() + " uses " + s.getSkillName() + "!");
                String skillAction = "skill" + (pick + 1);
                Point origin = eSprite.getLocation();
                // Move in front of player
                Point target = new Point(pSprite.getX() + 80, origin.y);

                moveSprite(eSprite, opponent.getName(), target, () -> {
                    if (opponent.getName().equals("Tyron")) {
                        currentSkillSound = UIFactory.playSound("/resources/Tyron_skill" + (pick + 1) + "SoundEffect.wav");
                    }
                    loadGif(eSprite, opponent.getName(), skillAction);
                    opponent.updateMana(-s.getManaCost());
                    int dmg = BattleLogic.calculateDamage(opponent, currentPlayer, s);
                    BattleLogic.processEffects(s, opponent, currentPlayer, dmg);
                    appendToLog("Deals " + dmg + " damage to " + currentPlayer.getName() + ".");
                    if (s.getSkillName().equals("Radiant Blessing")) {
                        appendToLog(opponent.getName() + " heals for 85 HP.");
                    }
                    if (s.getSkillName().contains("Reaver")) {
                        appendToLog(opponent.getName() + " heals for " + (int)(dmg * 0.25) + " HP.");
                    }
                    if (s.getSkillName().equals("Mana Burn")) {
                        appendToLog(currentPlayer.getName() + " loses 20 Mana from Mana Burn.");
                    }
                    if (s.getSkillName().contains("Guard")) {
                        appendToLog(opponent.getName() + " prepares to reduce damage taken by 45%.");
                    }
                    if (s.getSkillName().equals("Light Pillar")) {
                        appendToLog(currentPlayer.getName() + "'s damage dealt reduced by 40%.");
                    }
                    refresh();

                    new Timer(1200, e2 -> {
                        loadGif(pSprite, currentPlayer.getName(), "gothit");
                        if (currentPlayer.getGender().equals("female")) {
                            UIFactory.playSound("/resources/female_gothit.wav");
                        } else {
                            UIFactory.playSound("/resources/male_gothit.wav");
                        }
                        // Load idle after hit animation
                        new Timer(500, eHit -> {
                            loadGif(pSprite, currentPlayer.getName(), "idle");
                            ((Timer)eHit.getSource()).stop();
                        }).start();
                        moveSprite(eSprite, opponent.getName(), origin, () -> {
                            if (currentSkillSound != null) {
                                currentSkillSound.stop();
                            }
                            loadGif(eSprite, opponent.getName(), "idle");
                            loadGif(pSprite, currentPlayer.getName(), "idle");
                            if (currentTurnLabel != null) {
                                currentTurnLabel.setText(getTurnLabelText());
                            }
                            isBusy = false;
                            checkRoundOver();
                        });
                        ((Timer)e2.getSource()).stop();
                    }).start();
                });
            } else {
                opponent.updateMana(opponent.getManaPerTurn() * 3); refresh();
                appendToLog(opponent.getName() + " rests, gaining " + (opponent.getManaPerTurn() * 3) + " Mana.");
                if (currentTurnLabel != null) {
                    currentTurnLabel.setText(getTurnLabelText());
                }
                isBusy = false; toggleButtons(true);
            }
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void moveSprite(JLabel sprite, String name, Point dest, Runnable onDone) {
        if (!loadGifIfExists(sprite, name, "walk")) {
            loadGif(sprite, name, "idle");
        }
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
         if (opponent.getHp() <= 0) {
             appendToLog(currentPlayer.getName() + " wins the round!");
             if (isPvP) {
                 if (currentPlayer == player1) p1RoundsWon++; else p2RoundsWon++;
             } else p1RoundsWon++;
             updateRoundsDisplay();
             if (isPvP) {
                 if (p1RoundsWon >= 3) {
                     finalizeMatch("PLAYER 1 WINS!");
                 } else if (p2RoundsWon >= 3) {
                     finalizeMatch("PLAYER 2 WINS!");
                 } else resetRound();
             } else {
                 if (p1RoundsWon >= 3) {
                     if (isArcade) {
                         arcadeDefeats++;
                         if (currentArcadeIndex >= arcadeOpponents.length - 1) {
                             finalizeMatch("ARCADE COMPLETE! YOU DEFEATED ALL OPPONENTS!");
                         } else {
                             currentArcadeIndex++;
                             currentPlayer = player1;
                             p1RoundsWon = 0;
                             p2RoundsWon = 0;
                             currentTurnLabel.setText(getTurnLabelText());
                             selectNextArcadeOpponent();
                             JOptionPane.showMessageDialog(this, "Next Opponent: " + opponent.getName() + "!");
                             resetRound();
                         }
                     } else {
                         finalizeMatch("PLAYER 1 WINS!");
                     }
                 } else resetRound();
             }
         } else if (currentPlayer.getHp() <= 0) {
             appendToLog(opponent.getName() + " wins the round!");
             if (isPvP) {
                 if (currentPlayer == player2) p1RoundsWon++; else p2RoundsWon++;
             } else p2RoundsWon++;
             updateRoundsDisplay();
             if (isPvP) {
                 if (p1RoundsWon >= 3) {
                     finalizeMatch("PLAYER 1 WINS!");
                 } else if (p2RoundsWon >= 3) {
                     finalizeMatch("PLAYER 2 WINS!");
                 } else resetRound();
             } else {
                 if (p2RoundsWon >= 3) {
                     finalizeMatch("GAME OVER! Defeated by " + opponent.getName() + " at " + arcadeDefeats + " opponent(s) defeated.");
                 } else resetRound();
             }
        } else {
            if (isPvP) {
                swapPlayers();
            } else if (isBusy) {
                computerTurn();
            } else {
                isBusy = false; toggleButtons(true);
            }
        }
    }

    private void setupButtons() {
        actionPanel.removeAll();
        for (Skills s : currentPlayer.getSkills()) {
            JButton b = UIFactory.createStyledButton("<html><center><b>" + s.getSkillName() + "</b><br>" + s.getManaCost() + " MP</center></html>", new Color(70, 70, 70), new Color(40, 40, 40));
            b.setPreferredSize(new Dimension(220, 120));
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.addActionListener(e -> playerTurn(s));
            actionPanel.add(b);
        }
        JButton rest = UIFactory.createStyledButton("REST", new Color(0, 100, 0), new Color(0, 70, 0));
        rest.setPreferredSize(new Dimension(150, 120));
        rest.addActionListener(e -> {
            isBusy = true;
            if (turnTimer != null) turnTimer.stop();
            if (displayTimer != null) displayTimer.stop();
            toggleButtons(false);
            currentPlayer.updateMana(currentPlayer.getManaPerTurn() * 4); refresh();
            appendToLog(currentPlayer.getName() + " rests, gaining " + (currentPlayer.getManaPerTurn() * 4) + " Mana.");
            if (isPvP) {
                swapPlayers();
            } else {
                computerTurn();
            }
        });
        actionPanel.add(rest);

        JButton exit = UIFactory.createStyledButton("EXIT", new Color(150, 0, 0), new Color(100, 0, 0));
        exit.setPreferredSize(new Dimension(150, 120));
        exit.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to go back to the main menu?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                if (onExitListener != null) {
                    onExitListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "exit"));
                }
            }
        });
        actionPanel.add(exit);
        actionPanel.revalidate(); actionPanel.repaint();
    }

    private void resetRound() {
        try {
            StyledDocument doc = battleLog.getStyledDocument();
            SimpleAttributeSet separatorStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(separatorStyle, new Color(255, 200, 0));
            StyleConstants.setBold(separatorStyle, true);
            
            doc.insertString(doc.getLength(), "\n* ======== NEXT ROUND ======== *\n\n", separatorStyle);
            battleLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // More robust logging can be added here
        }

        appendToLog("Starting next round!");
        JOptionPane.showMessageDialog(this, "Next Round!");
        if (isPvP) {
            player1.updateHp(player1.getMaxHp()); player1.updateMana(player1.getMaxMana());
            player2.updateHp(player2.getMaxHp()); player2.updateMana(player2.getMaxMana());
        } else {
            currentPlayer = player1;
            currentTurnLabel.setText(getTurnLabelText());
            currentPlayer.updateHp(currentPlayer.getMaxHp()); currentPlayer.updateMana(currentPlayer.getMaxMana());
            opponent.updateHp(opponent.getMaxHp()); opponent.updateMana(opponent.getMaxMana());
            if (eSprite != null) {
                loadGif(eSprite, opponent.getName(), "idle");
            }
        }
        if (turnTimer != null) turnTimer.stop();
        if (displayTimer != null) displayTimer.stop();
        turnTimerLabel.setVisible(false);
        refresh();
        updateRoundsDisplay();
        if (isPvP) {
            startPvPTimer();
        }
        isBusy = false; toggleButtons(true);
    }

    private void finalizeMatch(String msg) {
        try {
            StyledDocument doc = battleLog.getStyledDocument();
            SimpleAttributeSet endStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(endStyle, new Color(255, 215, 0));
            StyleConstants.setBold(endStyle, true);
            StyleConstants.setFontSize(endStyle, 12);
            
            doc.insertString(doc.getLength(), "\n=====================================\n", endStyle);
            doc.insertString(doc.getLength(), "   * BATTLE CONCLUDED *\n", endStyle);
            doc.insertString(doc.getLength(), msg + "\n", endStyle);
            doc.insertString(doc.getLength(), "=====================================\n", endStyle);
            battleLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // More robust logging can be added here
        }
        
        JOptionPane.showMessageDialog(this, msg);
        if (turnTimer != null) turnTimer.stop();
        if (displayTimer != null) displayTimer.stop();
        turnTimerLabel.setVisible(false);
        if (onExitListener != null) {
            onExitListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "exit"));
        }
    }

    private JPanel createStatPanel(String name, JProgressBar hp, JProgressBar mana, int wins, int maxWins) {
        JPanel p = new JPanel(new GridLayout(4, 1, 0, 5));
        p.setOpaque(false);

        // Character name
        JLabel l = new JLabel(name.toUpperCase(), SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Impact", Font.PLAIN, 28));

        // HP Bar with label
        JPanel hpContainer = new JPanel(new BorderLayout(5, 0));
        hpContainer.setOpaque(false);
        JLabel hpLabel = new JLabel("HP:");
        hpLabel.setForeground(new Color(255, 100, 100));
        hpLabel.setFont(new Font("Arial", Font.BOLD, 13));
        hp.setForeground(new Color(255, 50, 50));
        hp.setBackground(new Color(50, 10, 10));
        hp.setBorder(BorderFactory.createLineBorder(new Color(200, 50, 50), 2));
        hp.setStringPainted(true);
        hp.setFont(new Font("Arial", Font.BOLD, 11));
        hp.setPreferredSize(new Dimension(0, 25));
        hpContainer.add(hpLabel, BorderLayout.WEST);
        hpContainer.add(hp, BorderLayout.CENTER);

        // Mana bar part
        JPanel manaBarPanel = new JPanel(new BorderLayout(5, 0));
        manaBarPanel.setOpaque(false);
        JLabel manaLabel = new JLabel("MANA:");
        manaLabel.setForeground(new Color(100, 180, 255));
        manaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        mana.setForeground(new Color(50, 50, 255));
        mana.setBackground(new Color(10, 10, 50));
        mana.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));
        mana.setStringPainted(true);
        mana.setFont(new Font("Arial", Font.BOLD, 11));
        mana.setPreferredSize(new Dimension(0, 25));
        manaBarPanel.add(manaLabel, BorderLayout.WEST);
        manaBarPanel.add(mana, BorderLayout.CENTER);

        // Rounds panel (medium size, below mana)
        JPanel roundsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        roundsPanel.setOpaque(false);
        for (int i = 0; i < maxWins; i++) {
            JLabel roundLabel = new JLabel(i < wins ? "●" : "○");
            roundLabel.setForeground(new Color(255, 220, 100));
            roundLabel.setFont(new Font("Arial", Font.BOLD, 20));
            roundsPanel.add(roundLabel);
        }

        p.add(l);
        p.add(hpContainer);
        p.add(manaBarPanel);
        p.add(roundsPanel);
        return p;
    }

    private void refresh() {
        if (isPvP) {
            pHP.setMaximum(player1.getMaxHp()); pHP.setValue(player1.getHp());
            pMana.setMaximum(player1.getMaxMana()); pMana.setValue(player1.getMana());
            eHP.setMaximum(player2.getMaxHp()); eHP.setValue(player2.getHp());
            eMana.setMaximum(player2.getMaxMana()); eMana.setValue(player2.getMana());
        } else {
            pHP.setMaximum(currentPlayer.getMaxHp()); pHP.setValue(currentPlayer.getHp());
            pMana.setMaximum(currentPlayer.getMaxMana()); pMana.setValue(currentPlayer.getMana());
            eHP.setMaximum(opponent.getMaxHp()); eHP.setValue(opponent.getHp());
            eMana.setMaximum(opponent.getMaxMana()); eMana.setValue(opponent.getMana());
        }
    }

    private void swapPlayers() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        opponent = (opponent == player1) ? player2 : player1;
        appendToLog("Now it's " + currentPlayer.getName() + "'s turn.");
        currentTurnLabel.setText(getTurnLabelText());
        setupButtons();
        refresh();
        isBusy = false;
        toggleButtons(true);

        // Ensure both sprites show idle animation and don't swap
        loadGif(pSprite, player1.getName(), "idle");
        loadGif(eSprite, player2.getName(), "idle");

        if (isPvP) {
            startPvPTimer();
        }
    }

    private void loadGif(JLabel l, String n, String a) {
        URL url = getClass().getResource("/resources/" + n + "_" + a + ".gif");
        if (url != null) l.setIcon(new ImageIcon(url));
    }

    private boolean loadGifIfExists(JLabel l, String n, String a) {
        URL url = getClass().getResource("/resources/" + n + "_" + a + ".gif");
        if (url != null) {
            l.setIcon(new ImageIcon(url));
            return true;
        }
        return false;
    }

    private void toggleButtons(boolean s) { for (Component c : actionPanel.getComponents()) c.setEnabled(s); }

    private String getTurnLabelText() {
        if (isPvP) {
            if (currentPlayer == player1) {
                return "PLAYER 1 TURN: " + currentPlayer.getName();
            } else {
                return "PLAYER 2 TURN: " + currentPlayer.getName();
            }
        } else {
            return "Current Turn: " + currentPlayer.getName();
        }
    }

    private Characters generateAI() {
        String[] ns = CharacterRegistry.getAllNames();
        return CharacterRegistry.getCharacter(ns[rand.nextInt(ns.length)]);
    }

    private String[] buildArcadeOpponents(String playerName) {
        return Arrays.stream(CharacterRegistry.getAllNames())
                     .filter(name -> !name.equals(playerName))
                     .toArray(String[]::new);
    }

    private void selectNextArcadeOpponent() {
        if (currentArcadeIndex < arcadeOpponents.length) {
            opponent = CharacterRegistry.getCharacter(arcadeOpponents[currentArcadeIndex]);
            if (eSprite != null) {
                loadGif(eSprite, opponent.getName(), "idle");
            }
            if (statContainer != null) {
                updateRoundsDisplay();
            }
            if (currentTurnLabel != null) {
                currentTurnLabel.setText(getTurnLabelText());
            }
        }
    }

    private void updateRoundsDisplay() {
         statContainer.removeAll();
         String p1Label = player1.getName();
         String p2Label = isPvP ? player2.getName() : (opponent != null ? opponent.getName() : "AI");
         statContainer.add(createStatPanel(p1Label, pHP, pMana, p1RoundsWon, 3));
         statContainer.add(createStatPanel(p2Label, eHP, eMana, p2RoundsWon, 3));
         statContainer.revalidate();
        statContainer.repaint();
    }

    private void appendToLog(String msg) {
        try {
            StyledDocument doc = battleLog.getStyledDocument();

            // Create different attribute styles
            SimpleAttributeSet victoryStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(victoryStyle, new Color(255, 215, 0)); // Gold
            StyleConstants.setBold(victoryStyle, true);
            StyleConstants.setFontSize(victoryStyle, 13);

            SimpleAttributeSet damageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(damageStyle, new Color(255, 80, 80)); // Red
            StyleConstants.setBold(damageStyle, true);

            SimpleAttributeSet healStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(healStyle, new Color(100, 255, 100)); // Green

            SimpleAttributeSet skillStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(skillStyle, new Color(100, 200, 255)); // Cyan
            StyleConstants.setBold(skillStyle, true);

            SimpleAttributeSet restStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(restStyle, new Color(255, 165, 0)); // Orange

            SimpleAttributeSet turnStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(turnStyle, new Color(255, 255, 150)); // Light Yellow
            StyleConstants.setBold(turnStyle, true);

            SimpleAttributeSet buffStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(buffStyle, new Color(200, 150, 255)); // Purple

            SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(defaultStyle, new Color(150, 200, 100)); // Light Green

            // Determine message type and format accordingly
            if (msg.contains("wins the round") || msg.contains("WINS")) {
                doc.insertString(doc.getLength(), "=== * ", null);
                doc.insertString(doc.getLength(), msg, victoryStyle);
                doc.insertString(doc.getLength(), " * ===\n", null);
            } else if (msg.contains("damage") || msg.contains("Deals")) {
                doc.insertString(doc.getLength(), "[DMG] ", null);
                doc.insertString(doc.getLength(), msg, damageStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else if (msg.contains("heals") || msg.contains("healing")) {
                doc.insertString(doc.getLength(), "[HP+] ", null);
                doc.insertString(doc.getLength(), msg, healStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else if (msg.contains("uses") || msg.contains("Skill")) {
                doc.insertString(doc.getLength(), "[SKL] ", null);
                doc.insertString(doc.getLength(), msg, skillStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else if (msg.contains("rests") || msg.contains("gaining")) {
                doc.insertString(doc.getLength(), "[RST] ", null);
                doc.insertString(doc.getLength(), msg, restStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else if (msg.contains("turn") || msg.contains("Now")) {
                doc.insertString(doc.getLength(), "[>>>] ", null);
                doc.insertString(doc.getLength(), msg, turnStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else if (msg.contains("reduces") || msg.contains("reduced") || msg.contains("prepares")) {
                doc.insertString(doc.getLength(), "[BUF] ", null);
                doc.insertString(doc.getLength(), msg, buffStyle);
                doc.insertString(doc.getLength(), "\n", null);
            } else {
                doc.insertString(doc.getLength(), msg + "\n", defaultStyle);
            }

            // Auto-scroll to bottom
            battleLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // More robust logging can be added here
        }
    }

    private void startPvPTimer() {
        if (turnTimer != null) turnTimer.stop();
        if (displayTimer != null) displayTimer.stop();
        
        // Create new timers for each turn (since old ones are stopped and can't restart)
        turnTimer = new Timer(30000, e -> {
            if (!isBusy) {
                if (displayTimer != null) displayTimer.stop();
                currentPlayer.updateMana(currentPlayer.getManaPerTurn() * 4);
                refresh();
                appendToLog(currentPlayer.getName() + " took too long and rests, gaining " + (currentPlayer.getManaPerTurn() * 4) + " Mana.");
                swapPlayers();
            }
        });
        turnTimer.setRepeats(false);
        
        remainingTime = 30;
        turnTimerLabel.setVisible(true);
        turnTimerLabel.setText("Time: 30");
        
        displayTimer = new Timer(1000, e -> {
            remainingTime--;
            turnTimerLabel.setText("Time: " + remainingTime);
            if (remainingTime <= 0) {
                displayTimer.stop();
            }
        });
        
        turnTimer.start();
        displayTimer.start();
    }

    public void setOnExitListener(ActionListener listener) {
        this.onExitListener = listener;
    }
}
