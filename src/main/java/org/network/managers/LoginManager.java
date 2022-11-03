package org.network.managers;

import org.network.ServerLogPanel;
import org.network.UserData;
import org.network.packet.LoginPacket;
import org.network.packet.LoginPacketType;

import java.util.*;

public class LoginManager {
    private static Map<Integer, UserData> userList = new HashMap<>();
    public static boolean checkUser(LoginPacket loginPacket) {
        Collection<UserData> usersData = userList.values();
        for (UserData userData : usersData){
            if (userData.userName.equals(loginPacket.username) && userData.password.equals(loginPacket.password)){
                ServerLogPanel.appendText(loginPacket.username + " Login success");
                return true;
            }
        }
        ServerLogPanel.appendText(loginPacket.username + " Login failed");
        return false;
    }
    public static void sendAccept(String target){

    }
    public static void createUser(LoginPacket loginPacket) {
        int i = 0;
        while (userList.containsKey(i)){
            i++;
        }
        try{
            UserData userData = new UserData();
            userData.password = loginPacket.password;
            userData.id = i;
            userData.userName = loginPacket.username;
            userList.put(i,userData);
        }
        catch (Exception exception){
            ServerLogPanel.appendText("User : " + loginPacket.username + " creation failed.");
            return;
        }
        ServerLogPanel.appendText("User : " + loginPacket.username + " has created. User identification Id : " + i);
    }
}
