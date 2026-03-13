package GUIBattle;

import javax.swing.*;
import java.awt.*;

public class FlippedLabel extends JLabel {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // This flips the image horizontally
        g2.translate(getWidth(), 0);
        g2.scale(-1, 1);
        super.paintComponent(g2);
    }
}