import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserListPane extends JPanel implements UserStatusListener {
    private final ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this); // Register for user status updates

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    openChatWindow(userListUI.getSelectedValue());
                }
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);
    }

    private void openChatWindow(String login) {
        MessagePane messagePane = new MessagePane(client, login);
        JFrame frame = new JFrame("Message: " + login);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.getContentPane().add(messagePane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void online(String login) {
        SwingUtilities.invokeLater(() -> {
            if (!userListModel.contains(login)) {
                userListModel.addElement(login);
            }
        });
    }

    @Override
    public void offline(String login) {
        SwingUtilities.invokeLater(() -> {
            userListModel.removeElement(login);
        });
    }


    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        if (client.connect() && client.login("guest", "guest")) {
            SwingUtilities.invokeLater(() -> {
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);
                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);
            });
            System.out.println("Login successful and user list displayed.");
        } else {
            System.err.println("Connection failed or login unsuccessful.");
        }
    }

}
