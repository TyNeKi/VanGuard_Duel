package GUIChooseChar;

import javax.swing.*;
import java.awt.*;
import Data.CharacterRegistry;
import GUIBattle.GUIBattleScreen;

public class GUICharacterSelection extends JFrame {
    public GUICharacterSelection() {
        setTitle("Select Your Vanguard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel charPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        String[] names = CharacterRegistry.getAllNames();

        for (String name : names) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> {
                new GUIBattleScreen(CharacterRegistry.getCharacter(name)).setVisible(true);
                dispose();
            });
            charPanel.add(btn);
        }
        add(charPanel, BorderLayout.CENTER);
    }
}