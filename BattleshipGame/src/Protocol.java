public class Protocol {
    static String send(String cmd, Coord c) {
        return c == null ? cmd + "\n" : cmd + ";" + c + "\n";
    }
}
