package gameengine;

import javax.swing.*;
import java.awt.*;

public class FlippedLabel extends JLabel {
    public boolean flip;
    public FlippedLabel(boolean flip) { this.flip = flip; }

    @Override
    protected void paintComponent(Graphics g) {
        if (flip) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(getWidth(), 0);
            g2.scale(-1, 1);
            super.paintComponent(g2);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }
}