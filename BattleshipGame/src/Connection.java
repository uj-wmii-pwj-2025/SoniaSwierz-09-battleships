import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Connection {
    BufferedReader in;
    BufferedWriter out;
    String lastSent;
    Socket socket;

    static Connection asServer(int port) throws Exception {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Czekam na połączenie na porcie " + port + "...");
        Socket s = ss.accept();
        System.out.println("Klient połączony!");
        return new Connection(s);
    }

    static Connection asClient(String host, int port) throws Exception {
        return new Connection(new Socket(host, port));
    }

    Connection(Socket s) throws Exception {
        this.socket = s;
        s.setSoTimeout(1000);
        in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
    }

    void disableTimeout() throws Exception {
        socket.setSoTimeout(0);
    }

    void send(String msg) throws Exception {
        lastSent = msg;
        //System.out.print("SEND: " + msg);
        out.write(msg);
        out.flush();
    }

    String recv() throws Exception {
        int tries = 0;
        while (tries < 3) {
            try {
                String m = in.readLine();
                if (m == null)
                    throw new IOException("Połączenie zerwane");
                // System.out.println("RECV: " + m);
                return m;
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout... ponawiam ostatnią wiadomość");
                if (lastSent != null) {
                    out.write(lastSent);
                    out.flush();
                }
                tries++;
            }
        }
        System.out.println("Błąd komunikacji");
        System.exit(1);
        return null;
    }
}