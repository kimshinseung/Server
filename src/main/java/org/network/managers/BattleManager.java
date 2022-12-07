package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.data.BattleData;
import org.network.data.BattlePocketData;
import org.network.data.PocketMonData;
import org.network.packet.UserBattlePacket;
import org.network.packet.UserChatPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleManager {
    private static Map<String,Integer> roomMap = new HashMap<>();
    private static Map<Integer, BattleData> roomBattleData = new HashMap<>();
    private static int roomId = 0;
    public static void handleBattlePacket(UserBattlePacket userBattlePacket){
        switch (userBattlePacket.commandType){
            case "REQUEST" -> sendBattleRequest(userBattlePacket);
            case "ACCEPT" -> createBattleService(userBattlePacket);
            default -> processBattlePacket(userBattlePacket);
        }
    }
    //요청 수락 외의 이벤트를 처리하는 코드
    private static void processBattlePacket(UserBattlePacket userBattlePacket) {
        switch (userBattlePacket.commandType){
            case "ATTACK" -> attackToTarget(userBattlePacket);
            case "ITEM" -> userItemToTarget(userBattlePacket);
            case "CHANGE" -> applyChangeBattlePacket(userBattlePacket);
        }
        if (userBattlePacket.commandType.equals("CHANGE")) return;
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        String opponentName = battleData.getOpponent(userBattlePacket.username);
        sendSwapAndWaitPacket(opponentName,userBattlePacket.username);
//        int roomId = roomMap.get(userBattlePacket.username);
//        BattleData battleData = roomBattleData.get(roomId);
//        if (!battleData.addBattleProgress(userBattlePacket)){
//            return;
//        }
//        for (UserBattlePacket ubp : battleData.playerCommand.values()){
//
//            battleData.playerCommand.replace(ubp.username,null);
//        }
//        if (isUserReadyToNextBehavior(userBattlePacket)){
//            ServerLogPanel.appendText("Both users are ready.");
//        }
    }

    private static void sendSwapAndWaitPacket(String turnUsername, String waitUsername) {
        ServerLogPanel.appendText("Change turn to "+turnUsername + " turn. Next turn is "+waitUsername + " turn");
        UserBattlePacket userBattlePacket = new UserBattlePacket(
                -1,
                "SERVER",
                "TURN",
                turnUsername,
                new ArrayList<>()
        );
        AcceptServer.sendObjectByUsername(turnUsername,userBattlePacket);
        userBattlePacket.target = waitUsername;
        userBattlePacket.commandType = "WAIT";
        AcceptServer.sendObjectByUsername(waitUsername,userBattlePacket);
    }

    private static void applyChangeBattlePacket(UserBattlePacket userBattlePacket) {
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        UserChatPacket battleLog = new UserChatPacket(
                -1,
                "Server",
                userBattlePacket.target + " has changed pocketmon to " + PocketMonData.monsterInfo.get(userBattlePacket.args.get(0)).getName(),
                "-ALL-"
        );
        int changedPocketDataIndex = battleData.selectPocketMonByIndex(userBattlePacket.username,userBattlePacket.args.get(0));
        if (changedPocketDataIndex == -1){
            //에러처리
        }
        List<Integer> args = new ArrayList<>();
        args.add(changedPocketDataIndex);
        userBattlePacket.args = args;
        for (String username : battleData.playerPocketMonList.keySet()){
            AcceptServer.sendObjectByUsername(username,battleLog);
            AcceptServer.sendObjectByUsername(username,userBattlePacket);
        }
        battleData.playerState.replace(userBattlePacket.username,"COMMAND");
        updatePocketMonHealth(battleData);
    }

    private static void userItemToTarget(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText(userBattlePacket.username + " used item that " + userBattlePacket.args.get(0));
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        battleData.giveHealToCurrentPocketMon(userBattlePacket);
        updatePocketMonHealth(battleData);
    }

    private static void attackToTarget(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText(userBattlePacket.username + " attack by index : " + userBattlePacket.args.get(0));
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        battleData.giveDamageToCurrentPocketMon(userBattlePacket);
        updatePocketMonHealth(battleData);
    }
    private static void updatePocketMonHealth(BattleData battleData){
        for (String username : battleData.playerPocketMonList.keySet()){
            List<Integer> args = new ArrayList<>();
            BattlePocketData playerData = battleData.getCurrentPocketDataByUsername(username);
            BattlePocketData opponentData = battleData.getCurrentPocketDataByUsername(battleData.getOpponent(username));
            args.add(playerData.getCurrentHealth());
            args.add(playerData.getMaxHealth());
            args.add(opponentData.getCurrentHealth());
            args.add(opponentData.getMaxHealth());
            //AcceptServer.sendObjectByUsername(username,battleLog);
            UserBattlePacket battleResultPacket = new UserBattlePacket(
                    -1,
                    "Server",
                    "HEALTH",
                    "-ALL-",
                    args
            );
            handleChangeRequest(username);
            handleGameOver(username);
            AcceptServer.sendObjectByUsername(username,battleResultPacket);
        }
    }

    private static void handleChangeRequest(String username) {
        int roomId = roomMap.get(username);
        BattleData battleData = roomBattleData.get(roomId);
        if (battleData.checkPlayerCurrentPocketDefeat(username)){
            ServerLogPanel.appendText(username + "'s pocketmon has defeated. send change packet.");
            List<Integer> args = battleData.getPlayerRemainPocketMon(username);
            UserBattlePacket battlePacket = new UserBattlePacket(-1,"SERVER","CHANGE_REQUEST",username,args);
            AcceptServer.sendObjectByUsername(username,battlePacket);
        }
    }

    private static void handleGameOver(String username){
        int roomId = roomMap.get(username);
        BattleData battleData = roomBattleData.get(roomId);
        if (battleData.checkPlayerDefeat(username)){
            ServerLogPanel.appendText(username + " is defeated");
            List<Integer> args = new ArrayList<>();
            UserBattlePacket userBattlePacket1 = new UserBattlePacket(-1, "SERVER", "EXIT", "ALL", args);
            AcceptServer.sendObjectToAll(userBattlePacket1);
        }
    }
    private static void createBattleService(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText("Create room by id " + " : [" + roomId +"]" +"[" +userBattlePacket.target+","+userBattlePacket.username+"]");
        roomMap.put(userBattlePacket.username,roomId);
        roomMap.put(userBattlePacket.target,roomId);

        List<String> roomUserList = new ArrayList<>();
        roomUserList.add(userBattlePacket.username);
        roomUserList.add(userBattlePacket.target);
        BattleData battleData = new BattleData(roomUserList);
        roomBattleData.put(roomId,battleData);
        //ServerLogPanel.appendText("Here");

        AcceptServer.sendObjectByUsername(userBattlePacket.username,userBattlePacket);
        AcceptServer.sendObjectByUsername(userBattlePacket.target,userBattlePacket);
        sendSwapAndWaitPacket(userBattlePacket.username,userBattlePacket.target);
        roomId++;
    }
    private static void sendBattleRequest(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText("Send battle request");
        AcceptServer.sendObjectByUsername(userBattlePacket.target,userBattlePacket);
    }
}
