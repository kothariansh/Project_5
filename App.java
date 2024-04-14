import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Set the look and feel to be the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the main GUI (for example, LoginWindow)
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}

