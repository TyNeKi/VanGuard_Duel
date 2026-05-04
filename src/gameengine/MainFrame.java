package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final GUIStartScreen startScreen;
    private GUICharacterSelection charSelectionScreen;
    private GUIMapSelection mapSelectionScreen;
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
        UIFactory.stopMusic();
        charSelectionScreen = new GUICharacterSelection(isPvp, isArcade);
        charSelectionScreen.setOnBackListener(e -> showStartScreen());
        charSelectionScreen.setOnSelectionCompleteListener(e -> {
            Characters p1 = charSelectionScreen.getPlayer1();
            Characters p2 = charSelectionScreen.getPlayer2();
            if (isArcade) {
                showBattleScreen(p1, p2, isPvp, isArcade, MapManager.getRandomMap());
            } else {
                showMapSelection(p1, p2, isPvp, isArcade);
            }
        });
        mainPanel.add(charSelectionScreen, "charSelect");
        cardLayout.show(mainPanel, "charSelect");
    }

    private void showMapSelection(Characters p1, Characters p2, boolean isPvp, boolean isArcade) {
        mapSelectionScreen = new GUIMapSelection();
        mapSelectionScreen.setOnBackListener(e -> cardLayout.show(mainPanel, "charSelect"));
        mapSelectionScreen.setOnMapSelectedListener(e -> {
            String selectedMap = mapSelectionScreen.getSelectedMap();
            showBattleScreen(p1, p2, isPvp, isArcade, selectedMap);
        });
        mainPanel.add(mapSelectionScreen, "mapSelect");
        cardLayout.show(mainPanel, "mapSelect");
    }

    private void showBattleScreen(Characters p1, Characters p2, boolean isPvp, boolean isArcade, String map) {
        UIFactory.stopMusic();
        playRandomBattleMusic();
        if (battleScreen != null) {
            mainPanel.remove(battleScreen);
        }

        if (isArcade) {
            battleScreen = new GUIBattleScreen(p1, true, map);
        } else if (isPvp) {
            battleScreen = new GUIBattleScreen(p1, p2, map);
        } else {
            battleScreen = new GUIBattleScreen(p1, map);
        }
        battleScreen.setOnExitListener(e -> showStartScreen());
        mainPanel.add(battleScreen, "battle");
        cardLayout.show(mainPanel, "battle");
    }

    private void showStartScreen() {
        UIFactory.stopMusic();
        if (battleScreen != null) {
            mainPanel.remove(battleScreen);
            battleScreen = null;
        }
        if (charSelectionScreen != null) {
            mainPanel.remove(charSelectionScreen);
            charSelectionScreen = null;
        }
        if (mapSelectionScreen != null) {
            mainPanel.remove(mapSelectionScreen);
            mapSelectionScreen = null;
        }
        UIFactory.playMusic("/resources/Background menu music.wav");
        cardLayout.show(mainPanel, "start");
    }

    private void playRandomBattleMusic() {
        Random random = new Random();
        int musicNumber = random.nextInt(3) + 1;
        UIFactory.playMusic("/resources/Battle" + musicNumber + "_music.wav");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
