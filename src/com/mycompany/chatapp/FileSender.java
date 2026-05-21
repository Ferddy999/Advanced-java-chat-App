package com.mycompany.chatapp;

import java.io.*;
import java.net.*;

public class FileSender {

    public static void sendFile(
            File file,
            String host,
            int port
    ) {

        try (

                Socket socket =
                        new Socket(host, port);

                FileInputStream fis =
                        new FileInputStream(file);

                DataOutputStream dos =
                        new DataOutputStream(
                                socket.getOutputStream()
                        )

        ) {

            // SEND FILE NAME
            dos.writeUTF(file.getName());

            // SEND FILE SIZE
            dos.writeLong(file.length());

            byte[] buffer =
                    new byte[4096];

            int bytesRead;

            while((bytesRead =
                    fis.read(buffer)) > 0) {

                dos.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            dos.flush();

            System.out.println(
                    "File sent successfully!"
            );

        } catch(Exception e) {

            System.out.println(e);
        }
    }
}