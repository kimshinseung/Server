package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.UserService;
import org.network.packet.UserChatPacket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ChatManager {
    public static void sendMessage(UserChatPacket userChatPacket){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 HH시 mm분");
        String createdMsg = "["+formatter.format(date)+"]"+"["+userChatPacket.username+"]"+userChatPacket.chat;
        ServerLogPanel.appendText("["+userChatPacket.target+"]"+createdMsg);
        userChatPacket.chat = createdMsg;
        if (userChatPacket.target.equals("-ALL-")){
            SendToAll(userChatPacket);
            return;
        }
        else {
            SendToTarget(userChatPacket);
        }
    }

    private static void SendToTarget(UserChatPacket userChatPacket) {
        Vector<UserService> userVec = AcceptServer.getUserVec();
        for (UserService userService : userVec){
            if (userService.userData.userName.equals(userChatPacket.target)){
                userService.SendMsg(userChatPacket);
                break;
            }

        }
    }

    private static void SendToAll(UserChatPacket userChatPacket) {
        Vector<UserService> userVec = AcceptServer.getUserVec();
        for (UserService userService : userVec){
            userService.SendMsg(userChatPacket);
        }
    }
}
