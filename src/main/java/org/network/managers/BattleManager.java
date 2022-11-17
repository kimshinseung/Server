package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.UserService;
import org.network.packet.UserBattlePacket;

public class BattleManager {
    public static void handleBattlePacket(UserBattlePacket userBattlePacket){
        switch (userBattlePacket.commandType){
            case "REQUEST" -> sendBattleRequest(userBattlePacket);
            case "ACCEPT" -> createBattleService(userBattlePacket);
        }
    }
    private static void createBattleService(UserBattlePacket userBattlePacket) {
        UserService user01 = AcceptServer.findUserByUsername(userBattlePacket.username);
        UserService user02 = AcceptServer.findUserByUsername(userBattlePacket.target);
        ServerLogPanel.appendText("Create room by id " + "" +"[" +userBattlePacket.target+","+userBattlePacket.username+"]");
    }
    private static void sendBattleRequest(UserBattlePacket userBattlePacket) {
        AcceptServer.sendObjectByUsername(userBattlePacket.target,userBattlePacket);
    }
}
