package gameengine;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class GUIStartScreen extends JFrame {

    public GUIStartScreen() {
        setTitle("VanGuard Duel - Main Menu");

        // --- THE FULL SCREEN FIX ---
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fills the laptop screen
        this.setResizable(false); // Prevents manual resizing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL bgURL = getClass().getResource("/resources/backgroundSample.gif");
                if (bgURL != null) {
                    Image img = new ImageIcon(bgURL).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        this.setContentPane(backgroundPanel);
        setupCenteredUI();
    }

    private void setupCenteredUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        URL imgURL = getClass().getResource("/resources/titleGameMainMenu.png");
        JLabel titleLabel = (imgURL != null) ? new JLabel(new ImageIcon(imgURL)) : new JLabel("VANGUARD DUEL");
        gbc.gridy = 0; add(titleLabel, gbc);

        JButton vsCompBtn = createMenuButton("Vs Computer");
        vsCompBtn.addActionListener(e -> {
            new GUICharacterSelection(false).setVisible(true);
            this.dispose();
        });
        gbc.gridy = 1; add(vsCompBtn, gbc);

        JButton pvpBtn = createMenuButton("Vs Player");
        pvpBtn.addActionListener(e -> {
            new GUICharacterSelection(true).setVisible(true);
            this.dispose();
        });
        gbc.gridy = 2; add(pvpBtn, gbc);

        JButton exitBtn = createMenuButton("Exit Game");
        exitBtn.setBackground(new Color(150, 0, 0));
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 3; add(exitBtn, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 70));
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIStartScreen().setVisible(true));
    }
}