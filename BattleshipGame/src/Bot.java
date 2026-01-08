import java.util.*;

public class Bot {
    private final Random rand = new Random();
    private final List<Coord> allCoords = new ArrayList<>();

    public Bot() {
        for (int row = 0; row < 10; row++)
            for (int column = 0; column < 10; column++)
                allCoords.add(new Coord(row, column));
    }

    public Coord nextShot() {
        int repeatProbability = 10;
        if (rand.nextInt(100) < repeatProbability) {
            int idx = rand.nextInt(allCoords.size());
            return allCoords.get(idx);
        } else {
            int idx = rand.nextInt(allCoords.size());
            Coord c = allCoords.get(idx);
            allCoords.remove(idx);
            return c;
        }
    }
}
