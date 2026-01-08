package chat_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChatGUI extends JFrame {
    private ChatAgent myAgent;
    private JTextArea chatArea;
    private JTextField inputField;
    private JList<String> userList;
    private DefaultListModel<String> listModel;

    public ChatGUI(ChatAgent a) {
        super(a.getLocalName());
        myAgent = a;
        
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Zona de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Lista de useri online (din DF)
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setPreferredSize(new Dimension(150, 0));
        add(new JScrollPane(userList), BorderLayout.EAST);

        // Zona de jos (Input + Butoane)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        JButton aiBtn = new JButton("AI Help"); // Buton pentru Pydantic AI

        JPanel btnPanel = new JPanel();
        btnPanel.add(sendBtn);
        btnPanel.add(aiBtn);

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actiuni
        sendBtn.addActionListener(ev -> sendMessage());
        aiBtn.addActionListener(ev -> askAI());
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Nu inchide tot app-ul, doar agentul
        
        // La închiderea ferestrei, ștergem agentul
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                myAgent.doDelete();
            }
        });
    }

    public void updateUserList(String[] users) {
        listModel.clear();
        for (String u : users) {
            if (!u.equals(myAgent.getName())) { // Nu ne punem pe noi în listă
                listModel.addElement(u);
            }
        }
    }

    public void appendMessage(String msg) {
        chatArea.append(msg + "\n");
    }

    private void sendMessage() {
        String content = inputField.getText();
        String receiver = userList.getSelectedValue();
        
        if (receiver != null && !content.isEmpty()) {
            myAgent.sendMessage(receiver, content);
            appendMessage("Me -> " + receiver + ": " + content);
            inputField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Select a user and type a message!");
        }
    }
    
    private void askAI() {
        // Cere ajutor AI-ului pentru ultimul mesaj primit sau textul curent
        String text = inputField.getText();
        if(text.isEmpty()) text = "Salut, ce faci?"; // Default test
        myAgent.askAssistant(text);
    }
}