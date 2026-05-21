package com.mycompany.chatapp;

import java.io.*;
import java.net.*;

public class FileServer {

    public static void main(String[] args) {

        try (

                ServerSocket serverSocket =
                        new ServerSocket(6000)

        ) {

            System.out.println(
                    "File Server Running..."
            );

            while(true) {

                Socket socket =
                        serverSocket.accept();

                new Thread(() ->
                        receiveFile(socket)
                ).start();
            }

        } catch(Exception e) {

            System.out.println(e);
        }
    }

    public static void receiveFile(
            Socket socket
    ) {

        try (

                DataInputStream dis =
                        new DataInputStream(
                                socket.getInputStream()
                        )

        ) {

            // FILE NAME
            String fileName =
                    dis.readUTF();

            // FILE SIZE
            long fileSize =
                    dis.readLong();

            File downloadsFolder =
                    new File("downloads");

            if(!downloadsFolder.exists()) {

                downloadsFolder.mkdir();
            }

            File outputFile =
                    new File(
                            downloadsFolder,
                            fileName
                    );

            FileOutputStream fos =
                    new FileOutputStream(
                            outputFile
                    );

            byte[] buffer =
                    new byte[4096];

            int bytesRead;

            long remaining =
                    fileSize;

            while((bytesRead =
                    dis.read(
                            buffer,
                            0,
                            (int)Math.min(
                                    buffer.length,
                                    remaining
                            )
                    )) > 0) {

                fos.write(
                        buffer,
                        0,
                        bytesRead
                );

                remaining -= bytesRead;

                if(remaining == 0) {

                    break;
                }
            }

            fos.close();

            System.out.println(
                    "Received: "
                    + fileName
            );

        } catch(Exception e) {

            System.out.println(e);
        }
    }
}