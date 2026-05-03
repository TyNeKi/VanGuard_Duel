package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private GUIStartScreen startScreen;
    private GUICharacterSelection charSelectionScreen;
    private GUIBattleScreen battleScreen;

    public MainFrame() {
        setTitle("VanGuard Duel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Initialize Screens ---
        startScreen = new GUIStartScreen();
        
        // --- Setup Listeners ---
        startScreen.setOnArcade(e -> showCharSelection(false, true));
        startScreen.setOnVsComp(e -> showCharSelection(false, false));
        startScreen.setOnPvp(e -> showCharSelection(true, false));

        // --- Add panels to the main card layout ---
        mainPanel.add(startScreen, "start");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "start");
    }

    private void showCharSelection(boolean isPvp, boolean isArcade) {
        startScreen.stopMusic();
        charSelectionScreen = new GUICharacterSelection(isPvp, isArcade);
        charSelectionScreen.setOnBackListener(e -> showStartScreen());
        charSelectionScreen.setOnSelectionCompleteListener(e -> {
            Characters p1 = charSelectionScreen.getPlayer1();
            Characters p2 = charSelectionScreen.getPlayer2();
            showBattleScreen(p1, p2, isPvp, isArcade);
        });
        mainPanel.add(charSelectionScreen, "charSelect");
        cardLayout.show(mainPanel, "charSelect");
    }

    private void showBattleScreen(Characters p1, Characters p2, boolean isPvp, boolean isArcade) {
        startScreen.stopMusic();
        if (isArcade) {
            battleScreen = new GUIBattleScreen(p1, true);
        } else if (isPvp) {
            battleScreen = new GUIBattleScreen(p1, p2);
        } else {
            battleScreen = new GUIBattleScreen(p1);
        }
        battleScreen.setOnExitListener(e -> showStartScreen());
        mainPanel.add(battleScreen, "battle");
        cardLayout.show(mainPanel, "battle");
    }

    private void showStartScreen() {
        startScreen.startMusic();
        cardLayout.show(mainPanel, "start");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
