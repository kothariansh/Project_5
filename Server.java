import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final int serverPort;
    private final List<ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server ready for connections...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            System.out.println("Error accepting client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    public synchronized void addWorker(ServerWorker worker) {
        workerList.add(worker);
    }

    public synchronized void removeWorker(ServerWorker worker) {
        workerList.remove(worker);
    }

    // Updated method to accept ServerWorker
    public void broadcast(String message, ServerWorker senderWorker) {
        for (ServerWorker worker : workerList) {
            if (!worker.equals(senderWorker)) { // Check object equality instead of login
                try {
                    worker.send(message);
                } catch (IOException e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }

    public boolean sendMessageToUser(String sendTo, String login, String body) {
        for (ServerWorker worker : workerList) {
            if (worker.getLogin() != null && worker.getLogin().equalsIgnoreCase(sendTo)) {
                try {
                    worker.send("msg " + login + ": " + body);
                    return true;
                } catch (IOException e) {
                    System.err.println("Error sending message to " + sendTo + ": " + e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    public boolean authenticateUser(String login, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(login) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read users file: " + e.getMessage());
        }
        return false;
    }
}
