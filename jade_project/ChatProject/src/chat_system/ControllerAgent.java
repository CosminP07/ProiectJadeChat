package chat_system;

import jade.core.Agent;
import jade.wrapper.PlatformController;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ControllerAgent extends Agent {
    protected void setup() {
        System.out.println("Controller pornit.");
        
        JFrame frame = new JFrame("System Controller");
        JButton killButton = new JButton("SHUTDOWN PLATFORM");
        
        killButton.addActionListener((ActionEvent e) -> {
            try {
                // Obține controller-ul platformei și omoară tot
                PlatformController container = getContainerController().getPlatformController();
                container.kill();
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.add(killButton);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }
}