import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RegistrationWindow extends JFrame {
    private JTextField usernameField = new JTextField(20);
    private JTextField nameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton registerButton = new JButton("Register");
    private JButton uploadButton = new JButton("Upload Picture");
    private File profilePicture;

    public RegistrationWindow() {
        super("Registration");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(uploadButton);
        panel.add(registerButton);

        uploadButton.addActionListener(this::uploadPicture);
        registerButton.addActionListener(this::registerUser);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }

    private void uploadPicture(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            profilePicture = fileChooser.getSelectedFile();
        }
    }

    private void registerUser(ActionEvent event) {
        String username = usernameField.getText();
        String name = nameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || name.isEmpty() || password.isEmpty() || profilePicture == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and upload a picture.");
            return;
        }

        // Assuming UserService class handles registration
        UserService userService = new UserService();
        if (userService.registerUser(username, password, name, profilePicture)) {
            JOptionPane.showMessageDialog(this, "Registration successful.");
            this.setVisible(false);
            new LoginWindow().setVisible(true);  // Open login window upon successful registration
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
        }
    }

    public static void main(String[] args) {
        new RegistrationWindow();
    }
}
