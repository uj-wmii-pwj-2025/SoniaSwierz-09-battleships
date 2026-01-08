public class Coord {
    int row, column;

    Coord(int row, int column) {
        this.row = row;
        this.column = column;
    }

    static Coord fromString(String s) {
        int column = s.charAt(0) - 'A';
        int row = Integer.parseInt(s.substring(1)) - 1;
        return new Coord(row, column);
    }

    @Override
    public String toString() {
        return "" + (char)('A' + column) + (row + 1);
    }
}
