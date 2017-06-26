package ru.fearofcode.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by maks on 6/26/2017.
 */
public class Client {
    private Socket socket;
    private Repeater repeater;

    private BufferedReader in;
    private PrintWriter out;


    public Client() {
        Scanner scan = new Scanner(System.in);

        try {
            socket = new Socket(Settings.ipAddressServer, Settings.portServer);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Please enter your nik name.");
            out.println(scan.nextLine());

            repeater = new Repeater();
            repeater.start();

            String newMessage = "";
            while(!"exit".equals(newMessage)){
                newMessage = scan.nextLine();
                out.println(newMessage);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            close();
        }


    }

    private void close() {
        try{
            repeater.setStop();
            in.close();
            out.close();
            socket.close();
        }catch(IOException e){
            System.err.println("Couldn't closed connection with server");
        }
    }

    private class Repeater extends Thread{
        private boolean stop = false;

        public void setStop() {
            stop = true;
        }

        @Override
        public void run(){
            try {
                while (!stop) {
                    String newMessage = in.readLine();
                    System.out.println(newMessage);
                }
            }catch(IOException e){
                System.err.println("Couldn't get message");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}