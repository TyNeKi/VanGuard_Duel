package gameengine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderboardManager {

    private static final String LEADERBOARD_FILE = "src/resources/leaderboard.json";

    public static class ScoreEntry {
        private final String name;
        private final int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    public static void addScore(String name, int score) {
        List<ScoreEntry> scores = getScores();
        scores.add(new ScoreEntry(name, score));
        
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }

        saveScores(scores);
    }

    public static List<ScoreEntry> getScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);

        if (!file.exists()) {
            return scores;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(LEADERBOARD_FILE)));
            if (content.isEmpty()) {
                return scores;
            }
            
            Pattern pattern = Pattern.compile("\\{\\s*\"name\":\\s*\"(.*?)\",\\s*\"score\":\\s*(\\d+)\\s*\\}");
            Matcher matcher = pattern.matcher(content);
            
            while (matcher.find()) {
                String name = matcher.group(1);
                int score = Integer.parseInt(matcher.group(2));
                scores.add(new ScoreEntry(name, score));
            }
        } catch (IOException e) {
            System.err.println("Error reading leaderboard file: " + e.getMessage());
            return new ArrayList<>();
        }
        
        return scores;
    }

    private static void saveScores(List<ScoreEntry> scores) {
        StringBuilder jsonBuilder = new StringBuilder("[\n");
        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry entry = scores.get(i);
            jsonBuilder.append("    {\n");
            jsonBuilder.append("        \"name\": \"").append(entry.getName()).append("\",\n");
            jsonBuilder.append("        \"score\": ").append(entry.getScore()).append("\n");
            jsonBuilder.append("    }");
            if (i < scores.size() - 1) {
                jsonBuilder.append(",\n");
            } else {
                jsonBuilder.append("\n");
            }
        }
        jsonBuilder.append("]");

        try (FileWriter file = new FileWriter(LEADERBOARD_FILE)) {
            file.write(jsonBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
