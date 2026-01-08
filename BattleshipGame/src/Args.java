public class Args {
    boolean server;
    int port;
    String host;
    String mapFile;
    boolean manual;

    static Args parse(String[] args) {
        Args a = new Args();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode":
                    a.server = args[++i].equals("server");
                    break;
                case "-port":
                    a.port = Integer.parseInt(args[++i]);
                    break;
                case "-map":
                    a.mapFile = args[++i];
                    break;
                case "-host":
                    a.host = args[++i];
                    break;
                case "-manual":
                    a.manual = true;
                    break;
            }
        }

        if (!a.server && a.host == null)
            throw new IllegalArgumentException("Client wymaga -host");

        return a;
    }
}