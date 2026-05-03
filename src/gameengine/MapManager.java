package gameengine;

import java.util.Random;

public class MapManager {

    private static final String[] MAPS = {
        "Battle1_background.png",
        "Battle2_background.png",
        "Battle3_background.png",
        "Battle4_background.png",
        "Battle5_background.png"
    };

    private static final Random rand = new Random();

    public static String[] getAllMaps() {
        return MAPS;
    }

    public static String getRandomMap() {
        return MAPS[rand.nextInt(MAPS.length)];
    }
}
