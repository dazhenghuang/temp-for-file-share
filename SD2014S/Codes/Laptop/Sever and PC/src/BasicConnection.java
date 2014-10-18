import java.net.*;
import java.io.*;

public class BasicConnection implements Connection {
    private InetAddress host;
    private int port;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public BasicConnection(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean openConnection() {
        /*
         * Handles the initialization of the socket as well as the reader and
         * writer.
         */
        try {
            this.socket = new Socket(this.host, this.port);
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            return true;

        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean closeConnection() {
        try {
            this.socket.close();
            this.writer.close();
            this.reader.close();
            return true;
        }
        catch(IOException e) {
            return false;
        }

    }

    public void sendMessage(String message) {
        // Might need to add some error handling here.
        this.writer.println(message);
    }

    public String getMessage() {
        try {
            return this.reader.readLine();
        }
        catch(IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

}
