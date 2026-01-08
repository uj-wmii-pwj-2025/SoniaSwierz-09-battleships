import java.util.Scanner;

public class Game {
    Args cfg;
    Board board;
    Board enemyBoard;
    Connection conn;
    Bot bot;
    Coord lastMyShot;
    Scanner scanner;

    Game(Args cfg, Board board, Connection conn) throws Exception {
        this.cfg = cfg;
        this.board = board;
        this.conn = conn;
        this.bot = new Bot();
        this.enemyBoard = Board.createTrackingBoard();
        this.scanner = new Scanner(System.in);

        if (cfg.manual) {
            conn.disableTimeout();
        }
    }

    Coord getNextShot() {
        if (cfg.manual) {
            while (true) {
                System.out.print("Twój ruch (np. A1, J5): ");
                String input = scanner.nextLine().trim().toUpperCase();
                try {
                    return Coord.fromString(input);
                } catch (Exception e) {
                    System.out.println("Błędne współrzędne! Spróbuj np. A1");
                }
            }
        } else {
            return bot.nextShot();
        }
    }

    void printEndGame(boolean won) {
        if (won) {
            System.out.print("Wygrana\n");
            enemyBoard.revealAllWater();
            enemyBoard.printEnemyCompetitionFormat();
        } else {
            System.out.print("Przegrana\n");
            enemyBoard.printEnemyCompetitionFormat();
        }

        System.out.println();
        board.print();
    }

    void play() throws Exception {
        board.print();

        if (cfg.manual) {
            System.out.println("TRYB MANUALNY: Wpisuj współrzędne ręcznie.");
        }

        if (!cfg.server) {
            lastMyShot = getNextShot();
            String msg = "start;" + lastMyShot + "\n";
            conn.send(msg);
        }

        while (true) {
            String msg = conn.recv();
            if (msg == null) break;

            System.out.println(msg.trim());

            String[] parts = msg.trim().split(";", 2);
            String resultOfMyShot = parts[0];

            if (resultOfMyShot.equals("ostatni zatopiony")) {
                enemyBoard.markEnemyBoard(lastMyShot, resultOfMyShot);
                printEndGame(true);
                return;
            }

            Coord enemyShotCoord = null;
            if (parts.length > 1) {
                enemyShotCoord = Coord.fromString(parts[1]);
            }

            if (!resultOfMyShot.equals("start") && lastMyShot != null) {
                enemyBoard.markEnemyBoard(lastMyShot, resultOfMyShot);
            }

            String myResponseResult = "";
            if (enemyShotCoord != null) {
                myResponseResult = board.shoot(enemyShotCoord);
            }

            if (myResponseResult.equals("ostatni zatopiony")) {
                System.out.println("ostatni zatopiony");
                conn.send("ostatni zatopiony\n");
                printEndGame(false);
                return;
            }

            if (cfg.manual) {
                System.out.println();
                enemyBoard.printEnemyCompetitionFormat();
                System.out.println();
            }

            lastMyShot = getNextShot();

            String response = myResponseResult + ";" + lastMyShot + "\n";
            System.out.print(response);
            conn.send(response);
        }
    }
}