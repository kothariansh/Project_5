import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {
    private final ChatClient client;
    private JTextField loginField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");

    public LoginWindow() {
        super("Login");
        this.client = new ChatClient("localhost", 8818);
        boolean connected = this.client.connect();

        if (!connected) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop further execution if connection fails
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 125);
        setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(loginField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("")); // Placeholder
        panel.add(loginButton);

        loginButton.addActionListener(this::doLogin);

        add(panel, BorderLayout.CENTER);
    }

    private void doLogin(ActionEvent e) {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Run login in a separate thread to avoid freezing the GUI
        new Thread(() -> {
            boolean loginSuccess = client.login(login, password);
            SwingUtilities.invokeLater(() -> {
                if (loginSuccess) {
                    // Switch to UserListPane if login is successful
                    UserListPane userListPane = new UserListPane(client);
                    JFrame frame = new JFrame("User List");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(400, 600);
                    frame.add(userListPane, BorderLayout.CENTER);
                    frame.setVisible(true);

                    setVisible(false); // Hide the login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid login/password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}
