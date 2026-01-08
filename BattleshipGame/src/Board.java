import MapGenerator.BattleshipBoardGenerator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int SIZE = 10;
    private final char[][] board;
    private int hitShipSegments = 0;

    public Board() {
        board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(board[i], '.');
        }
    }

    public static Board createTrackingBoard() {
        Board trackingBoard = new Board();
        for (int i = 0; i < trackingBoard.SIZE; i++) {
            Arrays.fill(trackingBoard.board[i], '?');
        }
        return trackingBoard;
    }

    public static Board generate() {
        Board generatedBoard = new Board();
        BattleshipBoardGenerator gen = new BattleshipBoardGenerator();
        String map = gen.generateMap();
        for (int i = 0; i < 100; i++) {
            generatedBoard.board[i / 10][i % 10] = map.charAt(i);
        }
        return generatedBoard;
    }

    public static Board fromString(String map) {
        if (map.length() != 100)
            throw new IllegalArgumentException("Mapa musi mieć 100 znaków");

        Board boardFromString = new Board();
        for (int i = 0; i < 100; i++) {
            boardFromString.board[i / 10][i % 10] = map.charAt(i);
        }
        return boardFromString;
    }

    public void print() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public void printEnemyCompetitionFormat() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                char c = board[i][j];
                if (c == '@') {
                    System.out.print('#');
                } else if (c == '~') {
                    System.out.print('.');
                } else if (c == '.') {
                    System.out.print('.');
                } else {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }

    private boolean isSunk(int row, int column, boolean[][] visited) {
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE)
            return true;

        if (visited[row][column])
            return true;

        if (board[row][column] == '.' || board[row][column] == '~' || board[row][column] == '?')
            return true;

        if (board[row][column] == '#')
            return false;

        visited[row][column] = true;
        return isSunk(row - 1, column, visited) &&
                isSunk(row + 1, column, visited) &&
                isSunk(row, column - 1, visited) &&
                isSunk(row, column + 1, visited);
    }

    public String shoot(Coord coord) {
        char cell = board[coord.row][coord.column];

        if (cell == '#' || cell == '@') {
            if (cell == '#') {
                board[coord.row][coord.column] = '@';
                hitShipSegments++;
            }

            int totalShipSegments = 20;
            if (hitShipSegments == totalShipSegments) return "ostatni zatopiony";
            if (isSunk(coord.row, coord.column, new boolean[SIZE][SIZE])) return "trafiony zatopiony";
            return "trafiony";

        } else if (cell == '.' || cell == '~') {
            board[coord.row][coord.column] = '~';
            return "pudło";
        }
        return "pudło";
    }

    public void markEnemyBoard(Coord coord, String result) {
        if (result.startsWith("trafiony")) {
            board[coord.row][coord.column] = '@';
            if (result.contains("zatopiony")) {
                markAroundSunk(coord.row, coord.column);
            }
        } else if (result.equals("pudło")) {
            if (board[coord.row][coord.column] == '?') {
                board[coord.row][coord.column] = '~';
            }
        }
    }

    public void revealAllWater() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == '?') {
                    board[i][j] = '.';
                }
            }
        }
    }

    private void markAroundSunk(int r, int c) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        List<Coord> shipSegments = new ArrayList<>();
        collectShipSegments(r, c, visited, shipSegments);

        for (Coord seg : shipSegments) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = seg.row + dr;
                    int nc = seg.column + dc;
                    if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                        if (board[nr][nc] == '?') {
                            board[nr][nc] = '.';
                        }
                    }
                }
            }
        }
    }

    private void collectShipSegments(int r, int c, boolean[][] visited, List<Coord> segments) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return;
        if (visited[r][c]) return;
        if (board[r][c] != '@') return;

        visited[r][c] = true;
        segments.add(new Coord(r, c));

        collectShipSegments(r - 1, c, visited, segments);
        collectShipSegments(r + 1, c, visited, segments);
        collectShipSegments(r, c - 1, visited, segments);
        collectShipSegments(r, c + 1, visited, segments);
    }
}