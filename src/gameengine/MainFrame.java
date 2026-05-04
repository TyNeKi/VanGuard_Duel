package gameengine;

import javax.swing.*;
import gamemodel.Characters;
import java.awt.*;
import javax.sound.sampled.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private GUIStartScreen startScreen;
    private GUICharacterSelection charSelectionScreen;
    private GUIMapSelection mapSelectionScreen;
    private GUIBattleScreen battleScreen;
    private GUILeaderboardScreen leaderboardScreen;
    private Clip backgroundMusic;

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
        startScreen.setOnLeaderboard(e -> showLeaderboardScreen());

        // --- Add panels to the main card layout ---
        mainPanel.add(startScreen, "start");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "start");
        playBackgroundMusic();
    }

    private void showCharSelection(boolean isPvp, boolean isArcade) {
        charSelectionScreen = new GUICharacterSelection(isPvp, isArcade);
        charSelectionScreen.setOnBackListener(e -> showStartScreen());
        charSelectionScreen.setOnSelectionCompleteListener(e -> {
            Characters p1 = charSelectionScreen.getPlayer1();
            Characters p2 = charSelectionScreen.getPlayer2();
            if (isArcade) {
                showBattleScreen(p1, p2, isPvp, true, MapManager.getRandomMap());
            } else {
                showMapSelection(p1, p2, isPvp, false);
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
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }
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

    private void showLeaderboardScreen() {
        leaderboardScreen = new GUILeaderboardScreen();
        leaderboardScreen.setOnBackListener(e -> showStartScreen());
        mainPanel.add(leaderboardScreen, "leaderboard");
        cardLayout.show(mainPanel, "leaderboard");
    }

    private void showStartScreen() {
        if (battleScreen != null) {
            battleScreen.stopMusic();
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
        if (leaderboardScreen != null) {
            mainPanel.remove(leaderboardScreen);
            leaderboardScreen = null;
        }
        
        // Re-create startScreen to ensure it's fresh
        mainPanel.remove(startScreen);
        startScreen = new GUIStartScreen();
        startScreen.setOnArcade(e -> showCharSelection(false, true));
        startScreen.setOnVsComp(e -> showCharSelection(false, false));
        startScreen.setOnPvp(e -> showCharSelection(true, false));
        startScreen.setOnLeaderboard(e -> showLeaderboardScreen());
        mainPanel.add(startScreen, "start");

        playBackgroundMusic();
        cardLayout.show(mainPanel, "start");
    }

    private void playBackgroundMusic() {
        if (backgroundMusic == null || !backgroundMusic.isRunning()) {
            if (backgroundMusic != null) {
                backgroundMusic.close();
            }
            backgroundMusic = UIFactory.playSound("/resources/Background menu music.wav", true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
