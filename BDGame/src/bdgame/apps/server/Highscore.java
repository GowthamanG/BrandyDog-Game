package bdgame.apps.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Creates and manages the highscore file
 */
public class Highscore {

    private String filePath;


    /**
     * Paramterless contructor:
     * Highscore file will be 'highscores' in the working directory.
     */
    public Highscore() {
        filePath = "highscores";
        checkForFile();
    }

    /**
     * Constructor with specified path
     *
     * @param filePath optional path to an existing or to be created highscore file
     */

    public Highscore(String filePath) {
        this.filePath = filePath;
        checkForFile();
    }

    /**
     * Increments the score of the player in the highscore file
     * by one. Creates new entry if the player won the first time.
     *
     * @param name name of the player who won
     */
    public void incrementScore(String name) {
        try {
            final File propertiesFile = new File(filePath);
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            String score = properties.getProperty(name);
            if (score == null) {
                properties.setProperty(name, "1");
            } else {
                properties.setProperty(name, "" + (Integer.parseInt(score) + 1));
            }
            properties.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads in the highscore properties file,
     * sorts entries by calling {@link Highscore#sortByValue(Map) sortByValue},
     * returns list as String.
     *
     * @return Highscore list as 'Player1=x\n Player2=y\n...'
     */
    public String getScoreboard() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            final File propertiesFile = new File(filePath);
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            Map<String, String> propertiesMap = (Map) properties;
            propertiesMap = sortByValue(propertiesMap);
            for (Map.Entry<String, String> e : propertiesMap.entrySet()) {
                stringBuilder.append(e);
                stringBuilder.append(";");
            }
            if (stringBuilder.length() != 0)
                stringBuilder.setLength(stringBuilder.length() - 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * check if file exists:
     * false -> create file
     */
    private void checkForFile() {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                final boolean newFile = f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sorts a given Map by value.
     *
     * @param map the map to sort
     * @param <K> the key type
     * @param <V> the value type
     * @return returns the given map in a sorted state.
     */
    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
