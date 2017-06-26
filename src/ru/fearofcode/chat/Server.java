package ru.fearofcode.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by maks on 6/26/2017.
 */

public class Server {
    private final List<Connection> connections = new LinkedList<>();
    private final List<String> messages = new LinkedList<>();
    private ServerSocket serverSocket;


    public Server() {
        try {
            serverSocket = new ServerSocket(Settings.portServer);


            while (true) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.start();
            }


        } catch (Exception e) {
            System.out.println("Error in Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class Connection extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private String name = "";


        public Connection(Socket socket){
            this.socket = socket;

            try {
                in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

            }catch(IOException e){
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run(){
            try {

                name = in.readLine();
                synchronized(messages) {
                    for (String message : messages) {
                        out.println(message);
                    }
                }

                String newMessage;
                String message;

                message = name + " - has come";
                synchronized (connections) {
                    for (Connection connection : connections) {
                        connection.out.println(message);
                    }
                    messages.add(message);
                }



                while (true) {
                    newMessage = in.readLine();
                    message = name + ": " + newMessage;

                    if ("exit".equals(newMessage)) break;
                    synchronized(connections){
                        for (Connection connection : connections){
                            if (connection.socket != socket) {
                                connection.out.println(message);
                            }
                        }
                        messages.add(message);
                    }
                }

                message = name + " - has left";
                synchronized(connections){
                    for (Connection connection : connections){
                        connection.out.println(message);
                    }
                    messages.add(message);
                }

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                close();
            }

        }




        public void close(){
            try {
                in.close();
                out.close();
                socket.close();

                connections.remove(this);
            } catch (Exception e) {
                System.err.println("Can't close connection");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}