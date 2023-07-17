package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ServerWindow extends JFrame{

   private JTextArea textArea;
    private JTextField adminTextArea;
   private JScrollPane chatScrollPane;
   private JList<Server.Client>members;
   private DefaultListModel<Server.Client> membersName;

   private Server server;
    public ServerWindow(Server server)  {
        this.server = server;
        setName("Server");
        setBackground(Color.BLACK);
        setSize(new Dimension(400,500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        textEditorClient();
        scroll();
        textEditorAdmin();
        membersUI();
    }
    private void membersUI()
    {
        membersName = new DefaultListModel<>();
        members = new JList<>(membersName);
        members.setSize(new Dimension(150,450));
        members.setBounds(250,0,400,450);
        members.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        members.setVisible(true);
        add(members);
    }
    public void addMember(Server.Client name)
    {
        membersName.addElement(name);
    }
    public void removeMember(Client name)
    {
        membersName.removeElement(name);
    }
    private void textEditorClient()
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
    private void clearText()
    {
        textArea.setText("");
        textArea.append("------------Server-------------\n");
    }
    private void textEditorAdmin()
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
        chatScrollPane = new JScrollPane(textArea);
        chatScrollPane.setBounds(0,0,250,435);

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
        this.add(chatScrollPane);
        this.add(adminTextArea);
    }

}
