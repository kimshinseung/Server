package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.UserData;
import org.network.packet.LoginPacket;
import org.network.packet.LoginPacketType;
import org.network.packet.UserListPacket;

import java.util.*;

public class LoginManager {
    private static Map<Integer, UserData> userList = new HashMap<>();
    public static int checkUser(LoginPacket loginPacket) {
        Collection<UserData> usersData = userList.values();
        for (UserData userData : usersData){
            if (userData.userName.equals(loginPacket.username) && userData.password.equals(loginPacket.password)){
                ServerLogPanel.appendText(loginPacket.username + " Login success");
                return userData.id;
            }
        }
        ServerLogPanel.appendText(loginPacket.username + " Login failed");
        return -1;
    }
    public static boolean createUser(LoginPacket loginPacket) {
        for (UserData userData : userList.values()){
            if (userData.userName.equals(loginPacket.username)){
                ServerLogPanel.appendText("account of username[" + loginPacket.username + "] already exist.");
                return false;
            }
        }
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
            ServerLogPanel.appendText("User : " + loginPacket.username + " has created. User identification Id : " + i);
            return true;
        }
        catch (Exception exception){
            ServerLogPanel.appendText("User : " + loginPacket.username + " creation failed.");
            return false;
        }
    }
}
