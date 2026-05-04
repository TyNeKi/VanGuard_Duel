package gameengine;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class GUIStartScreen extends JPanel {

    private ActionListener arcadeListener, vsCompListener, pvpListener, leaderboardListener;

    public GUIStartScreen() {
        setLayout(new GridBagLayout());
        setupCenteredUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        URL bgURL = getClass().getResource("/resources/backgroundSample.gif");
        if (bgURL != null) {
            Image img = new ImageIcon(bgURL).getImage();
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void setupCenteredUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        URL imgURL = getClass().getResource("/resources/titleGameMainMenu.png");
        if (imgURL != null) {
            JLabel titleLabel = new JLabel(new ImageIcon(imgURL));
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 0, 40, 0);
            add(titleLabel, gbc);
        }

        gbc.insets = new Insets(15, 0, 15, 0);

        JButton arcadeBtn = UIFactory.createStyledButton("Arcade Mode", new Color(255, 190, 0), new Color(150, 100, 0));
        arcadeBtn.addActionListener(e -> {
            if (arcadeListener != null) arcadeListener.actionPerformed(e);
        });
        gbc.gridy = 1;
        add(arcadeBtn, gbc);

        JButton vsCompBtn = UIFactory.createStyledButton("Vs Computer", new Color(0, 191, 255), new Color(0, 100, 150));
        vsCompBtn.addActionListener(e -> {
            if (vsCompListener != null) vsCompListener.actionPerformed(e);
        });
        gbc.gridy = 2;
        add(vsCompBtn, gbc);

        JButton pvpBtn = UIFactory.createStyledButton("Vs Player", new Color(50, 205, 50), new Color(20, 120, 20));
        pvpBtn.addActionListener(e -> {
            if (pvpListener != null) pvpListener.actionPerformed(e);
        });
        gbc.gridy = 3;
        add(pvpBtn, gbc);

        JButton leaderboardBtn = UIFactory.createStyledButton("Leaderboard", new Color(128, 128, 128), new Color(80, 80, 80));
        leaderboardBtn.addActionListener(e -> {
            if (leaderboardListener != null) leaderboardListener.actionPerformed(e);
        });
        gbc.gridy = 4;
        add(leaderboardBtn, gbc);

        JButton settingsBtn = UIFactory.createStyledButton("Settings", new Color(128, 128, 128), new Color(80, 80, 80));
        settingsBtn.addActionListener(e -> showSettingsDialog());
        gbc.gridy = 5;
        add(settingsBtn, gbc);

        JButton exitBtn = UIFactory.createStyledButton("Exit Game", new Color(255, 80, 80), new Color(150, 20, 20));
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 6;
        add(exitBtn, gbc);
    }

    public void setOnArcade(ActionListener listener) { this.arcadeListener = listener; }
    public void setOnVsComp(ActionListener listener) { this.vsCompListener = listener; }
    public void setOnPvp(ActionListener listener) { this.pvpListener = listener; }
    public void setOnLeaderboard(ActionListener listener) { this.leaderboardListener = listener; }

    private void showSettingsDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog settingsDialog = new JDialog(topFrame, "Settings", true);
        settingsDialog.setUndecorated(true);
        settingsDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 3));
        settingsDialog.getContentPane().setBackground(new Color(45, 45, 45));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel musicVolumeLabel = new JLabel("Music Volume:");
        musicVolumeLabel.setForeground(Color.WHITE);
        musicVolumeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(musicVolumeLabel, gbc);

        JSlider musicVolumeSlider = new JSlider(0, 100, (int) (UIFactory.musicVolume * 100));
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setPreferredSize(new Dimension(200, 20));
        musicVolumeSlider.addChangeListener(e -> {
            UIFactory.setMusicVolume(((JSlider) e.getSource()).getValue() / 100f);
        });
        gbc.gridx = 1;
        panel.add(musicVolumeSlider, gbc);

        JLabel sfxVolumeLabel = new JLabel("SFX Volume:");
        sfxVolumeLabel.setForeground(Color.WHITE);
        sfxVolumeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(sfxVolumeLabel, gbc);

        JSlider sfxVolumeSlider = new JSlider(0, 100, (int) (UIFactory.sfxVolume * 100));
        sfxVolumeSlider.setOpaque(false);
        sfxVolumeSlider.setPreferredSize(new Dimension(200, 20));
        sfxVolumeSlider.addChangeListener(e -> UIFactory.setSfxVolume(((JSlider) e.getSource()).getValue() / 100f));
        gbc.gridx = 1;
        panel.add(sfxVolumeSlider, gbc);

        JCheckBox muteCheckbox = new JCheckBox("Mute Music");
        muteCheckbox.setForeground(Color.WHITE);
        muteCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        muteCheckbox.setOpaque(false);
        muteCheckbox.setSelected(UIFactory.isMuted);
        muteCheckbox.addActionListener(e -> {
            UIFactory.setMuted(muteCheckbox.isSelected());
        });
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(muteCheckbox, gbc);

        JButton closeButton = new JButton("Go Back");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeButton.setBackground(new Color(200, 50, 50));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeButton.addActionListener(e -> settingsDialog.dispose());
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(closeButton, gbc);

        settingsDialog.add(panel);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(topFrame);
        settingsDialog.setVisible(true);
    }
}
