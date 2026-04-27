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

        JPanel charPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        charPanel.setOpaque(false);

        for (String name : CharacterRegistry.getAllNames()) {
            JButton btn = new JButton(name);
            final String selectedName = name;

            URL idleURL = getClass().getResource("/resources/" + name + "_idle.gif");
            if (idleURL != null) btn.setIcon(new ImageIcon(idleURL));

            btn.addActionListener(e -> {
                Characters selected = CharacterRegistry.getCharacter(selectedName);
                if (isArcade) {
                    new GUIBattleScreen(selected, true).setVisible(true);
                    this.dispose();
                } else if (!isPvP) {
                    new GUIBattleScreen(selected).setVisible(true);
                    this.dispose();
                } else {
                    if (player1 == null) {
                        player1 = selected;
                        header.setText("PLAYER 1: " + player1.getName() + " | PLAYER 2 CHOOSE YOUR VANGUARD");
                        header.setForeground(new Color(255, 200, 0));
                        // Disable the button that was chosen by Player 1
                        for (Component comp : charPanel.getComponents()) {
                            if (comp instanceof JButton && ((JButton)comp).getText().equals(selectedName)) {
                                comp.setEnabled(false);
                            }
                        }
                    } else {
                        new GUIBattleScreen(player1, selected).setVisible(true);
                        this.dispose();
                    }
                }
            });
            charPanel.add(btn);
        }
        add(charPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(200, 60));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setBackground(new Color(120, 120, 120));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            new GUIStartScreen().setVisible(true);
            this.dispose();
        });
        footer.add(backButton);
        add(footer, BorderLayout.SOUTH);
    }
}