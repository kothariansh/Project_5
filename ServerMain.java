import java.io.IOException;
public class ServerMain {
    public static void main(String[] args) {
        int port = 8818; // Default port, or parse from args if necessary
        Server server = new Server(port);
        server.start();
        System.out.println("Server started on port: " + port);
    }
}

