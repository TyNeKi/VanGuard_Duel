package GUIStart;

import GUIChooseChar.GUICharacterSelection; // Crucial import
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class GUIStartScreen extends JFrame {

    public GUIStartScreen() {
        setTitle("VanGuard Duel - Main Menu");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL bgURL = getClass().getResource("/resources/Background Vanguard.png");
                if (bgURL != null) {
                    Image img = new ImageIcon(bgURL).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(153, 101, 21));
                    g.fillRect(0, 0, getWidth(), getHeight());
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
        JLabel titleLabel;

        if (imgURL != null) {
            titleLabel = new JLabel(new ImageIcon(imgURL));
        } else {
            titleLabel = new JLabel("VANGUARD DUEL");
            titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
            titleLabel.setForeground(Color.WHITE);
        }
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        this.add(titleLabel, gbc);

        // 2. VS Computer Button
        JButton vsCompBtn = new JButton("Vs Computer");
        vsCompBtn.setPreferredSize(new Dimension(300, 70));
        vsCompBtn.setFont(new Font("Arial", Font.BOLD, 24));
        vsCompBtn.setFocusPainted(false);
        vsCompBtn.setBackground(new Color(50, 50, 50));
        vsCompBtn.setForeground(Color.BLACK);

        vsCompBtn.addActionListener(e -> {
            System.out.println("DEBUG: Vs Computer clicked. Transitioning...");
            try {

                GUICharacterSelection selectionScreen = new GUICharacterSelection();
                selectionScreen.setVisible(true);

                // Close this screen
                this.dispose();
            } catch (Exception ex) {
                System.err.println("CRASH DETECTED: Could not open Selection Screen.");
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        gbc.gridy = 1;
        this.add(vsCompBtn, gbc);

        // 3. Exit Button
        JButton exitBtn = new JButton("Exit Game");
        exitBtn.setPreferredSize(new Dimension(300, 70));
        exitBtn.setFont(new Font("Arial", Font.BOLD, 24));
        exitBtn.setFocusPainted(false);
        exitBtn.setBackground(new Color(150, 0, 0));
        exitBtn.setForeground(Color.BLACK);

        exitBtn.addActionListener(e -> System.exit(0));

        gbc.gridy = 2;
        this.add(exitBtn, gbc);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}


        java.awt.EventQueue.invokeLater(() -> {
            new GUIStartScreen().setVisible(true);
        });
    }
}