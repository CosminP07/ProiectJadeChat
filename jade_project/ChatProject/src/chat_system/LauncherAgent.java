package chat_system;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import javax.swing.*;
import java.awt.*;

public class LauncherAgent {
    static ContainerController mainContainer;

    public static void main(String[] args) {
        // 1. Pornire Platformă JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true"); // Pornește RMA (Sniffer, etc.)
        mainContainer = rt.createMainContainer(p);

        try {
            // Lansează Controller-ul (Shutdown button)
            AgentController ac = mainContainer.createNewAgent("Controller", "chat_system.ControllerAgent", null);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. GUI Launcher
        SwingUtilities.invokeLater(() -> createLauncherGUI());
    }

    private static void createLauncherGUI() {
        JFrame frame = new JFrame("JADE Launcher");
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        JTextField nameField = new JTextField(15);
        JButton launchBtn = new JButton("Launch Chat Agent");

        launchBtn.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                try {
                    // Creare dinamică a agentului
                    AgentController ac = mainContainer.createNewAgent(name, "chat_system.ChatAgent", null);
                    ac.start();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error launching agent: " + ex.getMessage());
                }
            }
        });

        frame.add(new JLabel("Agent Name:"));
        frame.add(nameField);
        frame.add(launchBtn);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}