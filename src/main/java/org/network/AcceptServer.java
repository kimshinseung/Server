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
    public static void removeUser(UserService userService) {
        try{
            userVec.removeElement(userService);
        }
        catch (Exception e){
            ServerLogPanel.appendText("User remove error.");
            ServerLogPanel.appendText(e.getMessage());
        }
        ServerLogPanel.appendText("Player exited server. Remaining Players : " + userVec.size());
    }

    public void createServer(){
        try {
            socket = new ServerSocket(CurrentPort);
        } catch (IOException e) {
            System.out.println("Server Creation failed");
            System.exit(0);
        }
    }
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
    public static void sendPacketByUsername(String username ,Object ob){
        for (UserService userService : userVec){
            if (userService.userData.userName.equals(username)){
                userService.sendObject(ob);
            }
        }
    }
    public static Vector<UserService> getUserVec() {
        return userVec;
    }
}