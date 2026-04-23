package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;
import java.net.URL;

public class GUICharacterSelection extends JFrame {
    private boolean isPvP;
    private boolean isArcade;
    private Characters player1 = null;

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

        JLabel header = new JLabel("CHOOSE YOUR VANGUARD", SwingConstants.CENTER);
        header.setFont(new Font("Impact", Font.PLAIN, 50));
        header.setForeground(Color.WHITE);
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
                        JOptionPane.showMessageDialog(this, "Player 1 ready!");
                    } else {
                        new GUIBattleScreen(player1, selected).setVisible(true);
                        this.dispose();
                    }
                }
            });
            charPanel.add(btn);
        }
        add(charPanel, BorderLayout.CENTER);
    }
}