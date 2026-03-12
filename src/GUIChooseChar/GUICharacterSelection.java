package GUIChooseChar;

import javax.swing.*;
import java.awt.*;
import GUIStart.GUIStartScreen; // Import this so the 'Back' button works

public class GUICharacterSelection extends JFrame {

    public GUICharacterSelection() {
        setTitle("Select Your Character");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JLabel header = new JLabel("CHOOSE YOUR VANGUARD", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 40));
        add(header, BorderLayout.NORTH);

        JPanel charPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        charPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        for (int i = 1; i <= 8; i++) {
            JButton charBtn = new JButton("Character " + i);
            charBtn.setFont(new Font("Arial", Font.BOLD, 18));
            charPanel.add(charBtn);
        }

        add(charPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        backBtn.setPreferredSize(new Dimension(200, 50));
        backBtn.addActionListener(e -> {
            new GUIStartScreen().setVisible(true);
            dispose();
        });
        add(backBtn, BorderLayout.SOUTH);
    }
}