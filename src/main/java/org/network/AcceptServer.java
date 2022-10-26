package org.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class AcceptServer extends Thread {
    public static final int BUF_LEN = 128;
    private ServerSocket socket;
    private static final Vector<UserService> userVec = new Vector<UserService>();

    public AcceptServer(int port) {
        try {
            socket = new ServerSocket(port);
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
                System.out.println("User entered server. Remaining members : " + userVec.size());
            } catch (IOException e) {
                System.exit(0);
            }
        }
    }

    public static Vector<UserService> getUserVec() {
        return userVec;
    }
}