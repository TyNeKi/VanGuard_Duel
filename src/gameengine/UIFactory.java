package gameengine;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.io.BufferedInputStream;
import java.net.URL;

public class UIFactory {

    public static float sfxVolume = 0.5f;
    public static float musicVolume = 0.5f;
    public static boolean isMuted = false;
    private static Clip currentMusicClip;

    public static JButton createStyledButton(String text, Color baseColor, Color darkColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int clip = 20;

                // Define the button shape
                GeneralPath path = new GeneralPath();
                path.moveTo(clip, 0);
                path.lineTo(width - clip, 0);
                path.lineTo(width, clip);
                path.lineTo(width, height - clip);
                path.lineTo(width - clip, height);
                path.lineTo(clip, height);
                path.lineTo(0, height - clip);
                path.lineTo(0, clip);
                path.closePath();

                // Determine colors based on button state
                Color c1 = baseColor;
                Color c2 = darkColor;
                if (getModel().isPressed()) {
                    c1 = darkColor.darker();
                    c2 = baseColor.darker();
                } else if (getModel().isRollover()) {
                    c1 = baseColor.brighter();
                    c2 = darkColor.brighter();
                }

                // Draw the main button gradient
                g2.setPaint(new GradientPaint(0, 0, c1, 0, height, c2));
                g2.fill(path);

                // Draw a subtle inner shadow for a 3D effect
                g2.setColor(new Color(0, 0, 0, 50));
                g2.setStroke(new BasicStroke(3));
                g2.draw(path);

                // Draw the border
                g2.setColor(darkColor.brighter());
                g2.setStroke(new BasicStroke(2));
                g2.draw(path);

                // Draw the text with a shadow for better readability
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, width, height);
                
                // Use HTML to render multi-line text
                JLabel label = new JLabel(getText());
                label.setSize(textRect.width, textRect.height);
                label.setFont(getFont());
                label.setForeground(Color.BLACK); // Shadow color
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                
                // Paint the shadow
                g2.translate(1, 1);
                label.paint(g2);
                
                // Paint the main text
                g2.translate(-1, -1);
                label.setForeground(Color.WHITE);
                label.paint(g2);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(400, 75));
        btn.setFont(new Font("SansSerif", Font.BOLD, 22)); // Adjusted font size
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playSound("/resources/button_hover.wav", false);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                playSound("/resources/button_click.wav", false);
            }
        });
        return btn;
    }

    public static Clip playSound(String soundFile, boolean isMusic) {
        try {
            URL url = UIFactory.class.getResource(soundFile);
            if (url == null) {
                System.err.println("Couldn't find file: " + soundFile);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioStream);
            
            if (isMusic) {
                if (currentMusicClip != null && currentMusicClip.isRunning()) {
                    currentMusicClip.stop();
                }
                currentMusicClip = soundClip;
                if (currentMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl control = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                    updateVolume(control, musicVolume);
                }
                currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl control = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                    // SFX are not muted by the global mute flag
                    float dB = (float) (Math.log(sfxVolume == 0.0 ? 0.0001 : sfxVolume) / Math.log(10.0) * 20.0);
                    control.setValue(dB);
                }
                soundClip.start();
            }
            
            return soundClip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusicClip != null && currentMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            updateVolume(control, musicVolume);
        }
    }
    
    public static void setMuted(boolean muted) {
        isMuted = muted;
        if (currentMusicClip != null && currentMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            updateVolume(control, musicVolume);
        }
    }

    public static void updateVolume(FloatControl control, float volume) {
        if (isMuted) {
            control.setValue(control.getMinimum());
        } else {
            float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            control.setValue(dB);
        }
    }
}
