package bdgame.apps.client;

import bdgame.apps.client.GUI.GUIClient;
import bdgame.apps.server.Server;

class Main {
    private static String ServerIP;
    private static String Port;
    private static String nickname;


    public static void main(String[] args) {
        nickname = "";
        if (args.length == 0) {
            args = new String[]{"client"};
        }
        Parser(args);

        if (args[0].equals("server")) {
            new Server(Short.parseShort(Port));
        }
        if (args[0].equals("client")) {
            GUIClient.initGUIClient(ServerIP, Port, nickname);
        }
    }

    private static void Parser(String[] args) {
        switch (args[0]) {
            case "server":
                if (args.length == 2) {
                    Port = args[1];
                } else {
                    throw new IllegalArgumentException("Paramater error!\nUsage: server <listenport>");
                }
                break;
            case "client":
                if (args.length == 2) {
                    String temp = args[1];
                    ServerIP = temp.substring(0, temp.indexOf(':'));
                    Port = temp.substring(temp.indexOf(':') + 1);
                } else if (args.length == 1) {
                    ServerIP = "";
                    Port = "";
                } else if (args.length == 3) {
                    String temp = args[1];
                    ServerIP = temp.substring(0, temp.indexOf(':'));
                    Port = temp.substring(temp.indexOf(':') + 1);
                    nickname = args[2];
                } else {
                    throw new IllegalArgumentException("Parameter error!\nUsage: client <serverip>:<serverport>");
                }
                break;
            default:
                throw new IllegalArgumentException("Parameter error!\nFor CLIClient: client <serverip>:<serverport>\n For Server: server <listenport>");
        }
    }
}
