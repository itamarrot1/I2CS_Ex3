package mygame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public static int[][] loadMap(String filename) {
        List<int[]> tempGrid = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\s+");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }
                tempGrid.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // העברת הרשימה למערך דו-ממדי
        int rows = tempGrid.size();
        int cols = tempGrid.get(0).length;
        int[][] grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            grid[i] = tempGrid.get(i);
        }

        return grid;
    }
}
