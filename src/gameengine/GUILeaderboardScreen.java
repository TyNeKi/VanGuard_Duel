package gameengine;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GUILeaderboardScreen extends JPanel {

    private ActionListener backListener;

    public GUILeaderboardScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Leaderboard Table
        JTable table = createLeaderboardTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JButton backButton = UIFactory.createStyledButton("Back to Menu", new Color(200, 0, 0), new Color(100, 0, 0));
        backButton.addActionListener(e -> {
            if (backListener != null) {
                backListener.actionPerformed(e);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createLeaderboardTable() {
        String[] columnNames = {"Rank", "Name", "Score"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 22));
        table.setRowHeight(40);
        table.setOpaque(false);
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.DARK_GRAY);
        
        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setBackground(new Color(20, 20, 20));
        header.setForeground(Color.YELLOW);
        
        // Center align cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Populate data
        List<LeaderboardManager.ScoreEntry> scores = LeaderboardManager.getScores();
        for (int i = 0; i < scores.size(); i++) {
            LeaderboardManager.ScoreEntry entry = scores.get(i);
            model.addRow(new Object[]{i + 1, entry.getName(), entry.getScore()});
        }
        
        return table;
    }

    public void setOnBackListener(ActionListener listener) {
        this.backListener = listener;
    }
}
