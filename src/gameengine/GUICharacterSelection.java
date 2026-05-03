package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class GUICharacterSelection extends JPanel {
    private boolean isPvP;
    private boolean isArcade;
    private Characters player1 = null;
    private Characters player2 = null;
    private JLabel header;
    private JPanel charPanel;

    private ActionListener onBackListener;
    private ActionListener onSelectionCompleteListener;

    public GUICharacterSelection(boolean isPvP, boolean isArcade) {
        this.isPvP = isPvP;
        this.isArcade = isArcade;
        
        setBackground(new Color(10, 25, 45));
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
            if (onBackListener != null) {
                onBackListener.actionPerformed(e);
            }
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

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (card.isEnabled()) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 0), 3));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (card.isEnabled()) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0), 2));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!card.isEnabled() || onSelectionCompleteListener == null) return;
                
                UIFactory.playSound("/resources/button_click.wav");
                Characters selected = CharacterRegistry.getCharacter(name);

                if (isArcade || !isPvP) {
                    player1 = selected;
                    onSelectionCompleteListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "selectionComplete"));
                } else {
                    if (player1 == null) {
                        player1 = selected;
                        header.setText("PLAYER 1: " + player1.getName() + " | PLAYER 2 CHOOSE YOUR VANGUARD");
                        header.setForeground(new Color(255, 200, 0));
                        
                        // Disable the selected card
                        card.setEnabled(false);
                        card.setBackground(new Color(80, 80, 80));
                        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    } else {
                        player2 = selected;
                        onSelectionCompleteListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "selectionComplete"));
                    }
                }
            }
        });

        return card;
    }

    // --- Methods for controller interaction ---

    public void setOnBackListener(ActionListener listener) {
        this.onBackListener = listener;
    }



    public void setOnSelectionCompleteListener(ActionListener listener) {
        this.onSelectionCompleteListener = listener;
    }

    public Characters getPlayer1() {
        return player1;
    }

    public Characters getPlayer2() {
        return player2;
    }
}
