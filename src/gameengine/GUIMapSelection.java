package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class GUIMapSelection extends JPanel {

    private ActionListener onMapSelectedListener;
    private ActionListener onBackListener;
    private String selectedMap;

    public GUIMapSelection() {
        setBackground(new Color(10, 25, 45));
        setLayout(new BorderLayout());

        JLabel header = new JLabel("CHOOSE YOUR ARENA", SwingConstants.CENTER);
        header.setFont(new Font("Impact", Font.PLAIN, 50));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(header, BorderLayout.NORTH);

        JPanel mapPanel = new JPanel(new GridLayout(1, 5, 30, 30));
        mapPanel.setOpaque(false);
        mapPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (String mapName : MapManager.getAllMaps()) {
            JPanel mapCard = createMapCard(mapName);
            mapPanel.add(mapCard);
        }
        add(mapPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton backButton = UIFactory.createStyledButton("Back", new Color(120, 120, 120), new Color(80, 80, 80));
        backButton.setPreferredSize(new Dimension(200, 60));
        backButton.addActionListener(e -> {
            if (onBackListener != null) {
                onBackListener.actionPerformed(e);
            }
        });
        footer.add(backButton);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createMapCard(String mapName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0), 2));
        card.putClientProperty("mapName", mapName);

        URL mapUrl = getClass().getResource("/resources/" + mapName);
        if (mapUrl != null) {
            ImageIcon icon = new ImageIcon(mapUrl);
            Image img = icon.getImage().getScaledInstance(300, 170, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img), SwingConstants.CENTER);
            card.add(imageLabel, BorderLayout.CENTER);
        }

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 0), 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0), 2));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectedMap = mapName;
                if (onMapSelectedListener != null) {
                    onMapSelectedListener.actionPerformed(null);
                }
            }
        });
        return card;
    }

    public void setOnMapSelectedListener(ActionListener listener) {
        this.onMapSelectedListener = listener;
    }

    public void setOnBackListener(ActionListener listener) {
        this.onBackListener = listener;
    }

    public String getSelectedMap() {
        return selectedMap;
    }
}
