import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessagePane extends JPanel implements ActionListener {
    private final ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String text = inputField.getText();
            listModel.addElement("You: " + text);
            client.send("msg " + login + " " + text); // Properly use the send method
            inputField.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // This method can be called to add messages to the message list from outside
    public void addMessage(String fromLogin, String msgBody) {
        SwingUtilities.invokeLater(() -> {
            listModel.addElement(fromLogin + ": " + msgBody);
        });
    }

}
