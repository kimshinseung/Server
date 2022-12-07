package org.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    //대상한테 오브젝트를 전송합니다.
    public static void sendObjectByUsername(String username ,Object ob){
        for (UserService userService : userVec){
            if (userService.userData.userName == null) continue;
            if (!userService.userData.userName.equals(username)) continue;
            try{
                userService.sendObject(ob);
            }
            catch (Exception e){
                //ignored
            }
        }
    }
    public static void sendObjectToAll(Object ob){
        for (UserService userService : userVec){
            if (userService.userData.userName == null) continue;
            try{
                userService.sendObject(ob);
            }
            catch (Exception e){
                //ignored
            }
        }
    }
    public static List<String> getUsernameList(){
        List<String> result = new ArrayList<>();
        for (UserService userService : userVec){
            result.add(userService.userData.userName);
        }
        return result;
    }
    public static UserService findUserByUsername(String username){
        for (UserService userService : userVec){
            if (userService.userData == null) continue;
            if (userService.userData.userName == null) continue;
            if (userService.userData.userName.equals(username)) return userService;
        }
        return null;
    }
    public static Vector<UserService> getUserVec() {
        return userVec;
    }
}