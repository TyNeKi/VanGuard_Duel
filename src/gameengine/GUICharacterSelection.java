package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;
import java.net.URL;

public class GUICharacterSelection extends JFrame {
    private boolean isPvP;
    private boolean isArcade;
    private Characters player1 = null;
    private JLabel header;
    private JPanel charPanel;

    public GUICharacterSelection(boolean isPvP, boolean isArcade) {
        this.isPvP = isPvP;
        this.isArcade = isArcade;
        setTitle("Select Your Vanguard");

        // --- FULL SCREEN SETTINGS ---
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(10, 25, 45));
        setLayout(new BorderLayout());

        String headerText = isPvP ? "PLAYER 1 CHOOSE YOUR VANGUARD" : "CHOOSE YOUR VANGUARD";
        header = new JLabel(headerText, SwingConstants.CENTER);
        header.setFont(new Font("Impact", Font.PLAIN, 50));
        header.setForeground(isPvP ? new Color(255, 200, 0) : Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(header, BorderLayout.NORTH);

        charPanel = new JPanel(new GridLayout(2, 4, 30, 30));
        charPanel.setOpaque(false);
        charPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (String name : CharacterRegistry.getAllNames()) {
            JPanel charCard = createCharacterCard(name);
            charPanel.add(charCard);
        }
        add(charPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton backButton = UIFactory.createStyledButton("Back", new Color(120, 120, 120), new Color(80, 80, 80));
        backButton.setPreferredSize(new Dimension(200, 60));
        backButton.addActionListener(e -> {
            new GUIStartScreen().setVisible(true);
            this.dispose();
        });
        footer.add(backButton);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCharacterCard(String name) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0), 2));
        card.setPreferredSize(new Dimension(200, 250));
        card.putClientProperty("name", name);

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        card.add(nameLabel, BorderLayout.SOUTH);

        URL idleURL = getClass().getResource("/resources/" + name + "_idle.gif");
        if (idleURL != null) {
            ImageIcon icon = new ImageIcon(idleURL);
            JLabel imageLabel = new JLabel(icon, SwingConstants.CENTER);
            card.add(imageLabel, BorderLayout.CENTER);
        }

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (card.isEnabled()) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 0), 3));
                    UIFactory.playSound("/resources/button_hover.wav");
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (card.isEnabled()) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0), 2));
                }
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!card.isEnabled()) return;
                UIFactory.playSound("/resources/button_click.wav");
                Characters selected = CharacterRegistry.getCharacter(name);
                if (isArcade) {
                    new GUIBattleScreen(selected, true).setVisible(true);
                    GUICharacterSelection.this.dispose();
                } else if (!isPvP) {
                    new GUIBattleScreen(selected).setVisible(true);
                    GUICharacterSelection.this.dispose();
                } else {
                    if (player1 == null) {
                        player1 = selected;
                        header.setText("PLAYER 1: " + player1.getName() + " | PLAYER 2 CHOOSE YOUR VANGUARD");
                        header.setForeground(new Color(255, 200, 0));
                        // Disable the card that was chosen by Player 1
                        for (Component comp : charPanel.getComponents()) {
                            if (comp instanceof JPanel && name.equals(((JComponent)comp).getClientProperty("name"))) {
                                comp.setEnabled(false);
                                comp.setBackground(new Color(80, 80, 80));
                                ((JComponent)comp).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                            }
                        }
                    } else {
                        new GUIBattleScreen(player1, selected).setVisible(true);
                        GUICharacterSelection.this.dispose();
                    }
                }
            }
        });

        return card;
    }
}
