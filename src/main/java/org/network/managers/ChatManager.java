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
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
        String createdMsg = "["+formatter.format(date)+"]"+"["+userChatPacket.username+"]"+(!userChatPacket.target.equals("-ALL-")?"[귓속말]":"")+userChatPacket.chat;
        ServerLogPanel.appendText("["+userChatPacket.target+"]"+createdMsg);
        userChatPacket.chat = createdMsg;
        if (userChatPacket.target.equals("-ALL-")){
            AcceptServer.sendObjectToAll(userChatPacket);
            return;
        }
        else {
            AcceptServer.sendObjectByUsername(userChatPacket.target, userChatPacket);
        }
    }
    public static void sendServerLogToAll(String msg){
        UserChatPacket userChatPacket = new UserChatPacket(
                -1,
                "Server",
                msg,
                "-ALL-"
        );
        AcceptServer.sendObjectToAll(userChatPacket);
    }
}
