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

    public static JButton createStyledButton(String text, Color baseColor, Color darkColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                int clip = 20;
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
                Color c1 = baseColor;
                Color c2 = darkColor;
                if (getModel().isPressed()) {
                    c1 = darkColor.darker();
                    c2 = baseColor.darker();
                } else if (getModel().isRollover()) {
                    c1 = baseColor.brighter();
                    c2 = darkColor.brighter();
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, height, c2));
                g2.fill(path);
                g2.setColor(darkColor.brighter());
                g2.setStroke(new BasicStroke(2));
                g2.draw(path);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, width, height);
                int x = (textRect.width - fm.stringWidth(getText())) / 2;
                int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(darkColor.darker());
                g2.drawString(getText(), x, y + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(400, 75));
        btn.setFont(new Font("SansSerif", Font.BOLD, 32));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playSound("/resources/button_hover.wav");
            }
            @Override
            public void mousePressed(MouseEvent e) {
                playSound("/resources/button_click.wav");
            }
        });
        return btn;
    }

    public static void playSound(String soundFile) {
        try {
            URL url = UIFactory.class.getResource(soundFile);
            if (url == null) {
                System.err.println("Couldn't find file: " + soundFile);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioStream);
            if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl sfxVolumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(sfxVolume == 0.0 ? 0.0001 : sfxVolume) / Math.log(10.0) * 20.0);
                sfxVolumeControl.setValue(dB);
            }
            soundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = volume;
    }
}
