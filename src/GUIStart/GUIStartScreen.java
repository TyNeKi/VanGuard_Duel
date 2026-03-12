package GUIStart;

import GUIChooseChar.GUICharacterSelection;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class GUIStartScreen extends javax.swing.JFrame {

    public GUIStartScreen() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);
        this.getContentPane().setLayout(new GridBagLayout());
        setupCenteredUI();
    }

    private void setupCenteredUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Using ClassLoader to look at the root of the project
        URL imgURL = Thread.currentThread().getContextClassLoader().getResource("resources/titleGameMainMenu.png");
        JLabel titleLabel;

        if (imgURL != null) {
            titleLabel = new JLabel(new ImageIcon(imgURL));
        } else {
            titleLabel = new JLabel("IMAGE NOT FOUND");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
            titleLabel.setForeground(Color.RED);
        }

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        this.add(titleLabel, gbc);

        JButton vsCompBtn = new JButton("Vs Computer");
        vsCompBtn.setPreferredSize(new Dimension(250, 60));
        vsCompBtn.setFont(new Font("Arial", Font.PLAIN, 20));

        vsCompBtn.addActionListener(e -> {
            GUICharacterSelection selectionScreen = new GUICharacterSelection();
            selectionScreen.setVisible(true);
            dispose();
        });

        gbc.gridy = 1;
        this.add(vsCompBtn, gbc);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(250, 60));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridy = 2;
        exitBtn.addActionListener(e -> System.exit(0));
        this.add(exitBtn, gbc);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 500, Short.MAX_VALUE)
        );
        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(GUIStartScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new GUIStartScreen().setVisible(true));
    }
}