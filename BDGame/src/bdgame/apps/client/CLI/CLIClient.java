package bdgame.apps.client.CLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CLIClient extends Thread {

    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("[HH:mm:ss]");
    private static BufferedReader inputStreamReader;
    private long lastPongReceived;
    private long lastPingSent;
    private long lastCommandReceived;
    private PrintStream printStream;

    private CLIClient(PrintStream printStream) {
        this.printStream = printStream;
    }

    public CLIClient(String ServerIP, short ServerPort) {
        try {
            // Start connection and initialize input-/outputstream
            Socket serverSocket = new Socket(ServerIP, ServerPort);
            BufferedReader inputLine = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            printStream = new PrintStream(serverSocket.getOutputStream());
            inputStreamReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
            boolean reachable = true;

            // choose name to enter server with
            System.out.println("Choose name: (default: " + System.getProperty("user.name") + ")");
            String name = inputLine.readLine().trim();
            if (name.isEmpty())
                name = System.getProperty("user.name");
            new Thread(new CLIClient(printStream)).start();
            printStream.println("join;" + name); //join request to server

            lastPingSent = 0;
            lastPongReceived = 0;
            lastCommandReceived = System.currentTimeMillis();
            while (reachable) {
                if (System.currentTimeMillis() - lastCommandReceived > 5000) {
                    send("ping");
                    lastPingSent = System.currentTimeMillis();
                }
                if ((lastPingSent > 0 && lastPongReceived > 0) && lastPingSent - lastPongReceived > 10000) {
                    reachable = false;
                }
                if (inputLine.ready()) {
                    String[] input = inputLine.readLine().trim().split(" ");
                    switch (input[0]) {
                        case "/help":
                        case "/h":
                        case "/?":
                            showHelp();
                            break;
                        case "/enter":
                            send("enter;" + input[1]);
                            break;
                        case "/users":
                            if (input.length > 1) {
                                if (!input[1].equals("all") && !input[1].equals("lobby"))
                                    System.out.println("Usage: /users all/lobby");
                                else
                                    send("show;" + input[1]);
                            } else {
                                send("show;all");
                            }
                            break;
                        case "/logout":
                        case "/quit":
                        case "/exit":
                            send("exit");
                            break;
                        case "/w":
                            if (input.length < 3) {
                                System.out.println("Usage: /w <username> <message>");
                            } else {
                                send("chat;" + input[1] + ";" + buildMessage(input, 2, " "));
                            }
                            break;
                        case "/nickname":
                            send("nick;" + input[1]);
                            break;
                        case "/all":
                            send("chat;all;" + buildMessage(input, 1, " "));
                            break;
                        default:
                            if (input[0].startsWith("@") && input.length >= 2) {
                                input[0] = input[0].replace("@", "");
                                send("chat;" + input[0] + ";" + buildMessage(input, 1, " "));
                            } else {
                                send("chat;lobby;" + buildMessage(input, 0, " "));
                            }
                            break;
                    }
                }
            }

            printStream.close();
            inputStreamReader.close();
            inputLine.close();
            serverSocket.close();
            System.out.println("Connection to Server closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) {
        printStream.println(message);
    }

    private void showHelp() {
        String helpString = "CLIClient commands:\n" +
                "/help /h /? - show this text\n" +
                "/users - list users on server\n" +
                "/logout /quit /exit - close connection to server\n" +
                "/w <user> <message> - message <user> <text> in private\n" +
                "/nickname <name> - change nickname to <name>\n" +
                "@<user> <message> - alternative way to whisper\n";
        System.out.println(helpString);
    }

    private String buildMessage(String[] inputArray, int startAt, String separator) {
        StringBuilder message = new StringBuilder();
        for (int i = startAt; i < inputArray.length; i++) {
            message.append(inputArray[i]).append(separator);
        }
        return message.toString();
    }


    public void run() {
        String serverResponse;
        Timestamp timestamp;
        try {
            while ((serverResponse = inputStreamReader.readLine()) != null) {
                switch (serverResponse) {
                    case "ping":
                        lastCommandReceived = System.currentTimeMillis();
                        send("pong");
                        break;
                    case "pong":
                        lastCommandReceived = System.currentTimeMillis();
                        lastPongReceived = System.currentTimeMillis();
                        lastPingSent = 0;
                        break;
                    default:
                        timestamp = new Timestamp(System.currentTimeMillis());
                        System.out.println(timestampFormat.format(timestamp) + " " + serverResponse);
                        break;
                }
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.out.println("Connection to Server closed.");
            } else
                e.printStackTrace();
        }
    }

}
