package org.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class AcceptServer extends Thread {
    public static int CurrentPort = 30000;
    public static final int BUF_LEN = 128;
    private ServerSocket socket;
    private static final Vector<UserService> userVec = new Vector<UserService>();
    public void createServer(){
        try {
            socket = new ServerSocket(CurrentPort);
        } catch (IOException e) {
            System.out.println("Server Creation failed");
            System.exit(0);
        }
    }
    @SuppressWarnings("unchecked")
    public void run() {
        while (true)
        {
            try {
                Socket clientSocket = socket.accept();
                UserService userService = new UserService(clientSocket);
                userVec.add(userService);
                userService.start();
                ServerLogPanel.appendText("Player entered server. Remaining Players : " + userVec.size());
            } catch (IOException e) {
                System.exit(0);
            }
        }
    }

    public static Vector<UserService> getUserVec() {
        return userVec;
    }
}