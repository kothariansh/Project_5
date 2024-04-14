import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame implements ChatInterface, MessageListener {
    private JTextArea chatArea;
    private JTextField inputField;
    private ChatClient chatClient;  // Reference to ChatClient

    public ChatWindow(ChatClient chatClient) {
        super("Chat Application");
        this.chatClient = chatClient;
        this.chatClient.addMessageListener(this);  // Register this window as a listener
        initializeComponents();
        setupWindow();
    }


    private void initializeComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSendMessage(inputField.getText());
                inputField.setText("");
            }
        });

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(inputField, BorderLayout.SOUTH);
    }

    private void setupWindow() {
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        appendMessage(fromLogin + ": " + msgBody);
    }

    private void onSendMessage(String message) {
        // This method now correctly uses the chatClient reference to send messages
        if (chatClient != null) {
            chatClient.sendMessage("targetUser", message);  // Replace "targetUser" with actual recipient logic or UI interaction
        }
    }
}
