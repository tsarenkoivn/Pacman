package view;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SerializableModel extends AbstractListModel<String> implements Serializable {
    private List<String[]> highScores;
    private static final String HIGHSCORE_FILE = "HighScores.txt";

    public SerializableModel() {
        highScores = new ArrayList<>();
        loadHighScores();
        sortHighScores();
    }

    private void loadHighScores() {
        File file = new File(HIGHSCORE_FILE);
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String name = "";
                    String points = "";
                    boolean namePart = true;
                    for (int j = 0; j < line.length(); j++) {
                        char c = line.charAt(j);
                        if (c == ':' && namePart) {
                            namePart = false;
                            continue;
                        }
                        if (namePart) {
                            name += c;
                        } else if (Character.isDigit(c)) {
                            points += c;
                        }
                    }
                    if (!name.trim().isEmpty() && !points.trim().isEmpty()) {
                        highScores.add(new String[]{name.trim(), points.trim()});
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading high scores file: " + e.getMessage());
            }
        }
    }

    private void sortHighScores() {
        Collections.sort(highScores, new Comparator<String[]>() {
            @Override
            public int compare(String[] s1, String[] s2) {
                try {
                    int score1 = Integer.parseInt(s1[1]);
                    int score2 = Integer.parseInt(s2[1]);
                    return Integer.compare(score2, score1);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    @Override
    public int getSize() {
        return highScores.size();
    }

    @Override
    public String getElementAt(int index) {
        if (index < 0 || index >= highScores.size()) {
            return null;
        }
        String[] scoreEntry = highScores.get(index);
        return String.format("%d. %-20s %s", index + 1, scoreEntry[0], scoreEntry[1]);
    }
}
