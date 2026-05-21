package com.mycompany.chatapp;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static final int PORT = 5000;

    private static Set<ClientHandler> clients =
            new HashSet<>();

    private static Set<String> usernames =
            new HashSet<>();

    public static void main(String[] args) {

        System.out.println("Chat Server Started...");

        try (ServerSocket serverSocket =
                     new ServerSocket(PORT)) {

            while (true) {

                Socket socket =
                        serverSocket.accept();

                System.out.println("New client connected");

                ClientHandler clientThread =
                        new ClientHandler(socket);

                clients.add(clientThread);

                new Thread(clientThread).start();
            }

        } catch (IOException e) {

            System.out.println(e);
        }
    }

    // BROADCAST TO ALL USERS
    public static void broadcast(
            String message,
            ClientHandler excludeUser
    ) {

        for (ClientHandler client : clients) {

            if (client != excludeUser) {

                client.sendMessage(message);
            }
        }
    }

    // SEND USER LIST
    public static void updateUserList() {

        String users =
                "USERS:" +
                String.join(",", usernames);

        for (ClientHandler client : clients) {

            client.sendMessage(users);
        }
    }

    // CLIENT HANDLER
    static class ClientHandler
            implements Runnable {

        private Socket socket;

        private PrintWriter writer;

        private BufferedReader reader;

        private String username;

        public ClientHandler(Socket socket) {

            this.socket = socket;
        }

        @Override
        public void run() {

            try {

                reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        )
                );

                writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

                // USERNAME
                username = reader.readLine();

                usernames.add(username);

                broadcast(
                        "🔵 " + username
                        + " joined the chat",
                        this
                );

                updateUserList();

                String message;

                while ((message =
                        reader.readLine()) != null) {

                    broadcast(
                            username + ": " + message,
                            this
                    );
                }

            } catch (IOException e) {

                System.out.println(e);

            } finally {

                try {

                    socket.close();

                } catch (IOException e) {

                    System.out.println(e);
                }

                clients.remove(this);

                usernames.remove(username);

                broadcast(
                        "🔴 " + username
                        + " left the chat",
                        this
                );

                updateUserList();
            }
        }

        public void sendMessage(String message) {

            writer.println(message);
        }
    }
}