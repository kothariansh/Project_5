import java.io.*;
import java.net.Socket;
import java.util.HashSet;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            System.out.println("Error handling client socket: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();  // Ensure outputStream is closed on exit
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.trim().split(" ", 3);
            if (tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd) && tokens.length == 3) {
                    handleLogin(tokens[1], tokens[2]);
                } else if ("msg".equalsIgnoreCase(cmd) && tokens.length == 3) {
                    handleMessage(tokens[1], tokens[2]);
                } else if ("join".equalsIgnoreCase(cmd) && tokens.length == 2) {
                    handleJoin(tokens[1]);
                } else if ("leave".equalsIgnoreCase(cmd) && tokens.length == 2) {
                    handleLeave(tokens[1]);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.broadcast("offline " + login, this);
        server.removeWorker(this);
        System.out.println(login + " logged off.");
    }

    private void handleLogin(String login, String password) throws IOException {
        if (authenticateUser(login, password)) {
            this.login = login;
            outputStream.write("ok login\n".getBytes());
            outputStream.flush();
            System.out.println(login + " logged in.");
            server.broadcast("online " + login, this);
        } else {
            outputStream.write("error login\n".getBytes());
            outputStream.flush();
            System.out.println("Failed login attempt for " + login);
        }
    }

    private void handleMessage(String sendTo, String body) throws IOException {
        boolean isDelivered = false;
        for (ServerWorker worker : server.getWorkerList()) {
            if (worker.getLogin() != null && worker.getLogin().equalsIgnoreCase(sendTo)) {
                String outMsg = "msg " + login + ": " + body + "\n";
                worker.send(outMsg);
                isDelivered = true;
                break; // Stop once the intended recipient is found
            }
        }
        if (!isDelivered) {
            outputStream.write(("error " + sendTo + " not logged in\n").getBytes());
        }
    }
    public void handleRegistration(String username, String password) throws IOException {
        if (!userExists(username)) {
            addUser(username, password);
            outputStream.write("ok registered\n".getBytes());
        } else {
            outputStream.write("error username exists\n".getBytes());
        }
    }

    private boolean userExists(String username) throws IOException {
        // Logic to check if the user already exists in users.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + "|")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addUser(String username, String password) throws IOException {
        // Append user to the file
        try (FileWriter fw = new FileWriter("users.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(username + "|" + password);
        }
    }


    private void handleJoin(String topic) {
        topicSet.add(topic);
        server.broadcast(login + " has joined " + topic, this);
    }

    private void handleLeave(String topic) {
        topicSet.remove(topic);
        server.broadcast(login + " has left " + topic, this);
    }

    public void send(String message) throws IOException {
        if (outputStream != null) {
            outputStream.write(message.getBytes());
            outputStream.flush();
        }
    }

    private boolean authenticateUser(String login, String password) throws IOException {
        return server.authenticateUser(login, password);  // Assuming the server has this method properly implemented
    }

    public String getLogin() {
        return login;
    }
}
