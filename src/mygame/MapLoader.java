package mygame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public static int[][] loadMap(String path) {
        List<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (lines.isEmpty()) return null;

        int rows = lines.size();
        int cols = lines.get(0).length();
        int[][] map = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            String currentLine = lines.get(i);
            for (int j = 0; j < cols; j++) {
                map[i][j] = Character.getNumericValue(currentLine.charAt(j));
            }
        }
        return map;
    }
}