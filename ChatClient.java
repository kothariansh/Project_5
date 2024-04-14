import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private BufferedReader serverIn;
    private final List<UserStatusListener> userStatusListeners = new ArrayList<>();
    private final List<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverName, serverPort);
            serverOut = socket.getOutputStream();
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startMessageReader();  // Start reading messages from server immediately after connecting
            System.out.println("Connected to server at " + serverName + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public boolean register(String username, String password) {
        try {
            String cmd = "register " + username + " " + password + "\n";
            serverOut.write(cmd.getBytes());
            serverOut.flush();

            String response = serverIn.readLine();
            System.out.println("Server response: " + response);
            return "ok registered".equalsIgnoreCase(response);
        } catch (IOException e) {
            System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }
    public synchronized void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public synchronized void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void send(String msg) throws IOException {
        if (serverOut != null) {
            serverOut.write(msg.getBytes());
            serverOut.flush();
        }
    }

    public synchronized void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public synchronized void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void sendMessage(String user, String message) {
        try {
            send("msg " + user + " " + message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        try {
            String cmd = "login " + username + " " + password + "\n";
            serverOut.write(cmd.getBytes());
            serverOut.flush();

            String response = serverIn.readLine();
            System.out.println("Server response: " + response);
            return "ok login".equalsIgnoreCase(response);
        } catch (IOException e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    private void startMessageReader() {
        new Thread(this::readMessageLoop).start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = serverIn.readLine()) != null) {
                handleServerMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from server: " + e.getMessage());
        }
    }

    private void handleServerMessage(String line) {
        if (line.startsWith("msg ")) {
            String[] parts = line.substring(4).split(":", 2);
            if (parts.length == 2) {
                String fromLogin = parts[0].trim();
                String msgBody = parts[1].trim();
                SwingUtilities.invokeLater(() -> messageListeners.forEach(listener -> listener.onMessage(fromLogin, msgBody)));
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        if (client.connect()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter [1] to register, [2] to login:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.print("Enter username for registration: ");
                String username = scanner.nextLine();
                System.out.print("Enter password for registration: ");
                String password = scanner.nextLine();
                if (client.register(username, password)) {
                    System.out.println("Registration successful");
                } else {
                    System.out.println("Registration failed");
                }
            } else if (choice == 2) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                if (client.login(username, password)) {
                    System.out.println("Login successful");
                } else {
                    System.out.println("Login failed");
                }
            }
            scanner.close();
        } else {
            System.out.println("Failed to connect to the server.");
        }
    }
}
