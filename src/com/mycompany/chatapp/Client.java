package com.mycompany.chatapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try {

            // CONNECT TO SERVER
            Socket socket =
                    new Socket("localhost", 5000);

            System.out.println("Connected to Server!");

            // INPUT
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            // OUTPUT
            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            // KEYBOARD INPUT
            Scanner scanner =
                    new Scanner(System.in);

            while (true) {

                System.out.print("You: ");

                String text = scanner.nextLine();

                // SEND MESSAGE
                writer.println(text);

                // RECEIVE RESPONSE
                String response = reader.readLine();

                System.out.println(response);
            }

        } catch (Exception e) {

            System.out.println(e);

        }
    }
}