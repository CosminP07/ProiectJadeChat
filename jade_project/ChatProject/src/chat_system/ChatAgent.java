package chat_system;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatAgent extends Agent {
    private ChatGUI gui;

    protected void setup() {
        // 1. Inițializare GUI
        gui = new ChatGUI(this);
        gui.setVisible(true);

        // 2. Înregistrare în Yellow Pages (DF)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-participant");
        sd.setName("JADEChat");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // 3. Comportament: Ascultă mesaje (CyclicBehaviour)
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String content = msg.getContent();
                    String sender = msg.getSender().getName();
                    
                    // Afișează în GUI
                    gui.appendMessage(sender + ": " + content);
                    
                    // Salvare în istoric (Persistență)
                    saveHistory(sender, content);
                } else {
                    block();
                }
            }
        });

        // 4. Comportament: Actualizează lista de useri (TickerBehaviour)
        addBehaviour(new TickerBehaviour(this, 5000) { // La fiecare 5 secunde
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("chat-participant");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    String[] names = new String[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        names[i] = result[i].getName().getName();
                    }
                    gui.updateUserList(names);
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
    }

    // Trimitere mesaj JADE
    public void sendMessage(String receiverName, String content) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(receiverName, AID.ISGUID));
        msg.setContent(content);
        send(msg);
        saveHistory("ME -> " + receiverName, content);
    }

    // Funcție de salvare istoric (Cerință persistență)
    private void saveHistory(String actor, String content) {
        try (FileWriter fw = new FileWriter("chat_history_" + getLocalName() + ".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(actor + ": " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void takeDown() {
        // Deregistrare din DF la închidere
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        gui.dispose();
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }
    
    // Apel către Pydantic AI (HTTP POST simplu)
    public void askAssistant(String text) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8000/assist");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                // JSON manual: {"message": "text"}
                String jsonInput = "{\"message\": \"" + text + "\"}";
                
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                // Răspunsul e JSON: {"suggestion": "..."}. Parsare simplă pentru demo
                String suggestion = response.toString().split("\":\"")[1].split("\"}")[0];
                gui.appendMessage("[AI Assistant]: " + suggestion);
                
            } catch (Exception e) {
                gui.appendMessage("[System]: AI Service unavailable.");
                e.printStackTrace();
            }
        }).start();
    }
}