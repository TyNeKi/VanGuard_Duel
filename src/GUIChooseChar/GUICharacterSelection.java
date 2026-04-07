package GUIChooseChar;

import javax.swing.*;
import java.awt.*;
import Data.CharacterRegistry;
import GUIBattle.GUIBattleScreen;
import GUIStart.GUIStartScreen;




public class GUICharacterSelection extends JFrame {
    public GUICharacterSelection() {
        setTitle("Select Your Vanguard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(10, 25, 45));
        setLayout(new BorderLayout());

        JLabel header = new JLabel("CHOOSE YOUR VANGUARD", SwingConstants.CENTER);
        header.setFont(new Font("Roboto", Font.BOLD, 50));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        add(header, BorderLayout.NORTH);

        JPanel charPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        charPanel.setOpaque(false);
        charPanel.setBorder(BorderFactory.createEmptyBorder(25,40,25,40));


        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton backButton = new JButton("Return Main Menu");
        backButton.setPreferredSize(new Dimension(300, 60));
        backButton.setBackground(new Color(50, 50, 50 ));
        backButton.setForeground(Color.ORANGE);
        backButton.setFont(new Font("Sans-Serif", Font.BOLD, 19));

        backButton.addActionListener(
                e -> {new GUIStartScreen().setVisible(true);
                        dispose();

        });
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        String[] names = CharacterRegistry.getAllNames();

        for (String name : names) {
            JButton btn = new JButton(name);


            java.net.URL bgURL = getClass().getResource("/resources/" + name + "_idle.gif ");
            if (bgURL != null) {
                btn.setIcon(new ImageIcon(bgURL));
            }


            btn.addActionListener(e -> {
                new GUIBattleScreen(CharacterRegistry.getCharacter(name)).setVisible(true);
                dispose();
            });


            charPanel.add(btn);
        }
        add(charPanel, BorderLayout.CENTER);
    }
}