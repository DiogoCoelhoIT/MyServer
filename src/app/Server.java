package app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private ServerSocket server;
    private Socket clientSocket;
    private ArrayList<Client> clients;
    private ExecutorService threadPool;
    private ServerWindow serverWindow;
    public Server() {
        try {
            server = new ServerSocket(8080);
            System.out.println("Server created in port 8080");
            clients = new ArrayList<>();
            serverWindow = new ServerWindow(this);
            threadPool = Executors.newFixedThreadPool(50);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String receiveMessage(Client client) throws IOException {
        BufferedReader in = client.in;
        String request = "";
        StringBuilder fullRequest = new StringBuilder();
        while ((request = in.readLine()) != null) {
            fullRequest.append(request);
            serverWindow.editTextClient(client.getName()+":"+request);
            if (!in.ready()) {
                break;
            }
        }
        return fullRequest.toString();

    }
    private void sendMessage(String message,Client actualClient)
    {
        String[] commands = message.split("");
        String command = commands[0];
        String[] words = message.split(" ");
        StringBuilder newMessage = new StringBuilder();
        boolean found = false;
        boolean search = false;
        boolean broadCast = false;
        for(Client client:clients) {
            switch (command) {
                case "/":
                    search = true;
                    if (client.getName().equalsIgnoreCase(words[0].substring(1))) {
                        newMessage.append(actualClient.getName()+": ");
                        for (int i = 1; i < words.length; i++) {

                            newMessage.append(words[i]);
                            newMessage.append(" ");
                        }
                        found = true;
                        client.getOut().println(newMessage);
                    } else if (words[0].substring(1).equalsIgnoreCase("list")) {
                        found = true;
                        if(clients.size() == 1)
                        {
                            actualClient.getOut().println("Only you here");
                        }else
                        if (!client.equals(actualClient)) {
                            actualClient.getOut().println(client.getName());
                        }
                    }else if(words[0].substring(1).equalsIgnoreCase("commands"))
                    {
                        if(client.equals(actualClient)) {
                            found = true;
                            String com = "/list to list all members currently connected to the server\n/'name' to send a private message to that person";
                            actualClient.getOut().println(com);
                        }
                    }
                    break;
                default:
                    broadCast = true;

            }
            }
        if (broadCast && (!message.equals("")&&!message.equals("\n")&&!message.equals("\r")))
        {
            broadCastMessage(message,actualClient);
        }
        if(search && !found)
        {
            actualClient.getOut().println("No user with that ID");
        }
    }
    public void start()
    {
        serverWindow.start();
        while(true) {
            serverWindow.editTextClient("Waiting for Client");
            try {
                clientSocket = server.accept();
                serverWindow.editTextClient("Client connected");
                Client client = new Client(clientSocket);
                clients.add(client);
                threadPool.submit(client);
            }
            catch (IOException e)
            {
                serverWindow.editTextClient("Client Connection Error");
                throw new RuntimeException(e);
            }
        }
    }

    private void broadCastMessage(String message,Client actualClient)
    {
        for(Client client:clients) {
            if(!client.equals(actualClient)) {
                client.getOut().println(actualClient.getName()+": "+message);
            }
        }

    }
    public void adminMessageSend(String n)
    {
        String[] words = n.split(" ");
        serverWindow.editTextClient("Admin: "+n);
        for(Client client:clients)
        {
            client.out.println("Admin: "+n);

            if(words[0].equalsIgnoreCase("/ban") && words.length>1)
            {
                if(words[1].equalsIgnoreCase(client.getName()))
                {
                        client.out.println("You have been banned!=X");
                        client.disconnect();
                        break;
                }
            }
        }
    }

    private boolean selectName(String selectedName,Client c)
    {
        boolean nameSelected = false;
        String name = removeEmojis(selectedName);
        if(name.split(" ").length>1 || name.split("").length >8 || name.split("").length<2)
        {
            c.out.println("Name have to be between 2 and 8 characters, and cant have spaces!");
            nameSelected =false;
        }else
        if (!clients.isEmpty()) {
            for (Client client : clients) {
                if (!client.equals(this) && client.getName().equalsIgnoreCase(name)) {
                    c.out.println("Name Taken");
                    nameSelected = false;
                    break;
                }
                nameSelected = true;
            }

        }else {
            nameSelected = true;
        }
        return nameSelected;
    }
    private String removeEmojis(String input)
    {

        String regex = "[\\x{1F600}-\\x{1F64F}" +
                "\\x{1F300}-\\x{1F5FF}" +
                "\\x{1F680}-\\x{1F6FF}" +
                "\\x{2600}-\\x{26FF}" +
                "\\x{2700}-\\x{27BF}" +
                "\\x{1F900}-\\x{1F9FF}" +
                "\\x{1F1E0}-\\x{1F1FF}" +
                "\\x{1FAD0}-\\x{1FAFF}" +
                "\\x{1F600}-\\x{1F64F}" +
                "\\x{1F680}-\\x{1F6FF}" +
                "\\x{1F1E0}-\\x{1F1FF}]+";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");

    }

    private class Client implements Runnable
    {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        private boolean nameSelected = false;

        public Client(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println("WELCOME TO THE SERVER");
                out.println("What's your name?");
                name = receiveMessage(this);
                while (!selectName(name,this)) {
                    out.println("What's your name?");
                    name = receiveMessage(this);
                }
                out.println("Welcome "+ name);
                broadCastMessage("Has Joined!",this);
            } catch (IOException e) {
                System.out.println("Client streams error");
                throw new RuntimeException(e);
            }
        }


        @Override
        public void run() {
            while (socket.isConnected())
            {
                String message;
                try {
                    message = receiveMessage(this);
                    if(message.equalsIgnoreCase("Exit"))
                    {

                        out.println("GoodBye! =D");
                        disconnect();
                    }
                    else {
                        sendMessage(message, this);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

                disconnect();
        }
        private void disconnect()
        {

            try {
                socket.close();
                in.close();
                out.close();
                clients.remove(this);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public PrintWriter getOut() {
            return out;
        }

        public String getName() {
            return name;
        }
    }

}
