package app;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket client;
    private Scanner sc;
    private BufferedReader in;
    private PrintWriter out;

    public Client() {

        try {
            client = new Socket("localhost", 8080);
            sc = new Scanner(System.in);
        } catch (IOException e) {
            System.out.println("Client Error Connection");
            throw new RuntimeException(e);
        }
    }

    private void startStreams()
    {
        try {
            out = new PrintWriter(client.getOutputStream());
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error with creation of streams");
            throw new RuntimeException(e);
        }

    }

    private void startInputThread()
    {
        Thread clientWorkerThread = new Thread(()-> {
            try {
                clientWorker();
            } catch (IOException e) {
                System.out.println("Error reading server input");
                throw new RuntimeException(e);
            }
        });
        clientWorkerThread.start();
    }

    private void startOutPutThread() throws IOException {
        while (client.isConnected())
        {
            String line = "";
            System.out.println("Whats your message to the server?");
            line = sc.nextLine();
            line = line;
            out.println(line);
            out.flush();
        }
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.out.println("Client error Closing");
            throw new RuntimeException(e);
        }

    }
    private void clientWorker() throws IOException {
        while (client.isConnected()) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (!in.ready()) {
                    break;
                }
            }

        }
    }
    public void start()
    {

        try {
            startStreams();
            startInputThread();
            startOutPutThread();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
