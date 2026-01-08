import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        Args cfg = Args.parse(args);

        Board myBoard;
        if (cfg.mapFile != null) {
            try {
                String content = Files.readString(Paths.get(cfg.mapFile));
                content = content.replaceAll("\\s","");
                myBoard = Board.fromString(content);
            } catch (Exception e) {
                System.out.println("Nie udało się wczytać mapy, generuję losową");
                myBoard = Board.generate();
            }
        } else {
            myBoard = Board.generate();
        }

        myBoard.print();

        Connection conn;

        if (cfg.server)
            conn = Connection.asServer(cfg.port);
        else
            conn = Connection.asClient(cfg.host, cfg.port);


        Game game = new Game(cfg, myBoard, conn);
        game.play();
    }
}