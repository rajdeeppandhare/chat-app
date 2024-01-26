import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // Declare components
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending Request to Server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection Done!");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI() {
        SwingUtilities.invokeLater(() -> {
            heading.setFont(font);
            messageArea.setFont(font);
            messageInput.setFont(font);
            heading.setIcon(new ImageIcon("lett him cook.PNG"));
            heading.setHorizontalTextPosition(SwingConstants.CENTER);
            heading.setVerticalTextPosition(SwingConstants.BOTTOM);
            heading.setHorizontalAlignment(SwingConstants.CENTER);
            heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            messageArea.setEditable(false);
            messageInput.setHorizontalAlignment(SwingConstants.CENTER);

            setLayout(new BorderLayout());

            add(heading, BorderLayout.NORTH);
            JScrollPane jScrollPane = new JScrollPane(messageArea);
            add(jScrollPane, BorderLayout.CENTER);
            add(messageInput, BorderLayout.SOUTH);

            setTitle("Client Messenger[END]");
            setSize(700, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader Started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Server terminated the chat!");
                        JOptionPane.showMessageDialog(this, "Server Terminated the Chat!");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: " + msg + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection Closed!");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client...");
        SwingUtilities.invokeLater(() -> new Client());
    }
}
