package com.mycompany.chatapp;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatGUI extends JFrame {

    JTextArea chatArea;

    JTextField messageField;

    JButton sendButton;

    JButton emojiButton;

    JButton imageButton;

    JButton fileButton;

    JProgressBar progressBar;

    JLabel avatarLabel;

    Socket socket;

    BufferedReader reader;

    PrintWriter writer;

    String username;

    public ChatGUI() {

        // USERNAME
        username = JOptionPane.showInputDialog(
                this,
                "Enter Username"
        );

        // WINDOW
        setTitle("Advanced Chat App - " + username);

        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        // MAIN PANEL
        JPanel mainPanel =
                new JPanel(new BorderLayout());

        // CHAT AREA
        chatArea = new JTextArea();

        chatArea.setEditable(false);

        chatArea.setLineWrap(true);

        chatArea.setWrapStyleWord(true);

        chatArea.setBackground(
                new Color(25,25,25)
        );

        chatArea.setForeground(Color.WHITE);

        chatArea.setFont(
                new Font("Consolas",
                        Font.PLAIN,
                        15)
        );

        JScrollPane scrollPane =
                new JScrollPane(chatArea);

        // MESSAGE FIELD
        messageField = new JTextField();

        messageField.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        15)
        );

        messageField.setBackground(
                new Color(45,45,45)
        );

        messageField.setForeground(Color.WHITE);

        // BUTTONS
        sendButton =
                new JButton("SEND");

        emojiButton =
                new JButton("😀");

        imageButton =
                new JButton("📷");

        fileButton =
                new JButton("📁");

        // PROGRESS BAR
        progressBar =
                new JProgressBar();

        progressBar.setStringPainted(true);

        // AVATAR
        avatarLabel = new JLabel();

        avatarLabel.setPreferredSize(
                new Dimension(60,60)
        );

        try {

            ImageIcon avatar =
                    new ImageIcon(
                            "resources/avatar1.png"
                    );

            Image scaled =
                    avatar.getImage()
                            .getScaledInstance(
                                    50,
                                    50,
                                    Image.SCALE_SMOOTH
                            );

            avatarLabel.setIcon(
                    new ImageIcon(scaled)
            );

        } catch(Exception e) {

            System.out.println(e);
        }

        // TOP PANEL
        JPanel topPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        topPanel.setBackground(
                new Color(35,35,35)
        );

        JLabel userLabel =
                new JLabel(username);

        userLabel.setForeground(Color.WHITE);

        userLabel.setFont(
                new Font("Arial",
                        Font.BOLD,
                        16)
        );

        topPanel.add(avatarLabel);

        topPanel.add(userLabel);

        // BUTTON PANEL
        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout()
                );

        buttonPanel.add(emojiButton);

        buttonPanel.add(imageButton);

        buttonPanel.add(fileButton);

        buttonPanel.add(sendButton);

        // BOTTOM PANEL
        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout()
                );

        bottomPanel.add(progressBar,
                BorderLayout.NORTH);

        bottomPanel.add(messageField,
                BorderLayout.CENTER);

        bottomPanel.add(buttonPanel,
                BorderLayout.EAST);

        // ADD COMPONENTS
        mainPanel.add(topPanel,
                BorderLayout.NORTH);

        mainPanel.add(scrollPane,
                BorderLayout.CENTER);

        mainPanel.add(bottomPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        // CONNECT
        connectToServer();

        // SEND BUTTON
        sendButton.addActionListener(
                e -> sendMessage()
        );

        // ENTER KEY
        messageField.addActionListener(
                e -> sendMessage()
        );

        // EMOJI BUTTON
        emojiButton.addActionListener(
                e -> openEmojiMenu()
        );

        // IMAGE BUTTON
        imageButton.addActionListener(
                e -> sendImage()
        );

        // FILE BUTTON
        fileButton.addActionListener(
                e -> sendFile()
        );

        // RECEIVE THREAD
        new Thread(
                () -> receiveMessages()
        ).start();
    }

    // CONNECT TO SERVER
    private void connectToServer() {

        try {

            socket =
                    new Socket(
                            "localhost",
                            5000
                    );

            reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            // SEND USERNAME
            writer.println(username);

            appendMessage(
                    "SYSTEM",
                    "Connected to server"
            );

        } catch(Exception e) {

            appendMessage(
                    "SYSTEM",
                    "Connection Failed"
            );
        }
    }

    // SEND MESSAGE
    private void sendMessage() {

        String message =
                messageField.getText().trim();

        if(!message.isEmpty()) {

            writer.println(message);

            appendMessage(
                    "ME",
                    message
            );

            messageField.setText("");
        }
    }

    // RECEIVE MESSAGES
    private void receiveMessages() {

        try {

            String message;

            while((message =
                    reader.readLine()) != null) {

                appendRawMessage(message);

                playNotificationSound();
            }

        } catch(Exception e) {

            appendMessage(
                    "SYSTEM",
                    "Disconnected"
            );
        }
    }

    // EMOJI MENU
    private void openEmojiMenu() {

        String[] emojis = {

                "😀",
                "😂",
                "🔥",
                "❤️",
                "😎",
                "👍",
                "🥳",
                "💯"
        };

        String emoji =
                (String)
                        JOptionPane.showInputDialog(

                                this,

                                "Choose Emoji",

                                "Emoji Picker",

                                JOptionPane.PLAIN_MESSAGE,

                                null,

                                emojis,

                                emojis[0]
                        );

        if(emoji != null) {

            messageField.setText(
                    messageField.getText()
                            + emoji
            );
        }
    }

    // SEND IMAGE
    private void sendImage() {

        JFileChooser chooser =
                new JFileChooser();

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Images",
                        "png",
                        "jpg",
                        "jpeg"
                )
        );

        int result =
                chooser.showOpenDialog(this);

        if(result ==
                JFileChooser.APPROVE_OPTION) {

            File file =
                    chooser.getSelectedFile();

            new Thread(() -> {

                try {

                    progressBar.setValue(0);

                    FileSender.sendFile(
                            file,
                            "localhost",
                            6000
                    );

                    for(int i = 0;
                        i <= 100;
                        i += 10) {

                        progressBar.setValue(i);

                        Thread.sleep(100);
                    }

                    appendMessage(
                            "SYSTEM",
                            "Image sent: "
                            + file.getName()
                    );

                } catch(Exception e) {

                    System.out.println(e);
                }

            }).start();
        }
    }

    // SEND FILE
    private void sendFile() {

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if(result ==
                JFileChooser.APPROVE_OPTION) {

            File file =
                    chooser.getSelectedFile();

            new Thread(() -> {

                try {

                    progressBar.setValue(0);

                    FileSender.sendFile(
                            file,
                            "localhost",
                            6000
                    );

                    for(int i = 0;
                        i <= 100;
                        i += 10) {

                        progressBar.setValue(i);

                        Thread.sleep(100);
                    }

                    appendMessage(
                            "SYSTEM",
                            "File sent: "
                            + file.getName()
                    );

                } catch(Exception e) {

                    System.out.println(e);
                }

            }).start();
        }
    }

    // PLAY SOUND
    private void playNotificationSound() {

        try {

            File soundFile =
                    new File(
                            "resources/notification.wav"
                    );

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            soundFile
                    );

            Clip clip =
                    AudioSystem.getClip();

            clip.open(audio);

            clip.start();

        } catch(Exception e) {

            System.out.println(e);
        }
    }

    // APPEND FORMATTED MESSAGE
    private void appendMessage(
            String sender,
            String message
    ) {

        String time =
                new SimpleDateFormat(
                        "HH:mm:ss"
                ).format(new Date());

        chatArea.append(
                "[" + time + "] "
                        + sender
                        + ": "
                        + message
                        + "\n"
        );

        chatArea.setCaretPosition(
                chatArea.getDocument()
                        .getLength()
        );
    }

    // RAW MESSAGE
    private void appendRawMessage(
            String message
    ) {

        String time =
                new SimpleDateFormat(
                        "HH:mm:ss"
                ).format(new Date());

        chatArea.append(
                "[" + time + "] "
                        + message
                        + "\n"
        );

        chatArea.setCaretPosition(
                chatArea.getDocument()
                        .getLength()
        );
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new ChatGUI()
                        .setVisible(true)
        );
    }
}