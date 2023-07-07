package app;

import app.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ServerWindow extends JFrame{

   private JTextArea textArea;
    private JTextField adminTextArea;
   private JScrollPane scrollPane;
   private Server server;
    public ServerWindow(Server server)  {
        this.server = server;
        setName("Server");
        setBackground(Color.BLACK);
        setSize(new Dimension(250,500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        textEditorClient();
        scroll();
        textEditorAdmin();
    }
    public void textEditorClient()
    {
        textArea = new JTextArea();
        textArea.setVisible(true);
        textArea.setEditable(false);
        textArea.setBounds(0,0,235,435);
        textArea.setText("------------Server-------------\n");
        textArea.setCaretColor(this.getBackground());
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.green);
        textArea.setBorder(BorderFactory.createLineBorder(Color.green,2));
    }
    public void clearText()
    {
        textArea.setText("");
        textArea.append("------------Server-------------\n");
    }
    public void textEditorAdmin()
    {
        adminTextArea = new JTextField();
        adminTextArea.setVisible(true);
        adminTextArea.setEditable(true);
        adminTextArea.setBounds(2,435,246,40);
        adminTextArea.setBackground(Color.BLACK);
        adminTextArea.setForeground(Color.green);
        adminTextArea.setCaretColor(Color.green);
        adminTextArea.setBorder(BorderFactory.createLineBorder(Color.GREEN,2));

        adminTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = getAdminText();
                    if(message.equalsIgnoreCase("/clear"))
                    {
                        clearText();
                    }else {
                        server.adminMessageSend(message);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


    }
    public void scroll()
    {
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0,0,250,435);

    }
    public void editTextClient(String text)
    {
        textArea.append(text+"\n");
    }
    public String getAdminText()
    {
       String s = adminTextArea.getText();
       adminTextArea.setText("");
       return s;
    }

    public void start()
    {
        this.setVisible(true);
        this.add(scrollPane);
        this.add(adminTextArea);
    }

}
