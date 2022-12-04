package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.UserService;
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

    private static boolean isUserReadyToNextBehavior(UserBattlePacket userBattlePacket)
    {
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        for (String state : battleData.playerState.values()){
            if (!state.equals("COMMAND")){
                return false;
            }
        }
        return true;
    }

    //요청 수락 외의 이벤트를 처리하는 코드
    private static void processBattlePacket(UserBattlePacket userBattlePacket) {
        switch (userBattlePacket.commandType){
            case "ATTACK" -> attackToTarget(userBattlePacket);
            case "ITEM" -> userItemToTarget(userBattlePacket);
            case "CHANGE" -> applyChangeBattlePacket(userBattlePacket);
        }
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
            gameover(username);
            AcceptServer.sendObjectByUsername(username,battleResultPacket);
        }
    }
    private static void gameover(String username){
        int roomId = roomMap.get(username);
        BattleData battleData = roomBattleData.get(roomId);
        if (battleData.checkPlayerDefeat(username)){
            List<Integer> args = new ArrayList<>();
            UserBattlePacket userBattlePacket1 = new UserBattlePacket(-1, "SERVER", "EXIT", "ALL", args);
            AcceptServer.sendObjectToAll(userBattlePacket1);
        }
//        int count=0;
//        for(int i=0;i<3;i++){
//            if(battleData.playerPocketMonList.get(username).get(i).isDead==true){
//                count+=1;
//            }
//            if(count==3) {
//                List<Integer> args = new ArrayList<>();
//                UserBattlePacket userBattlePacket1 = new UserBattlePacket(-1, "SERVER", "EXIT", "ALL", args);
//                AcceptServer.sendObjectToAll(userBattlePacket1);
//            }
//        }
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

        roomId++;

    }
    private static void sendBattleRequest(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText("Send battle request");
        AcceptServer.sendObjectByUsername(userBattlePacket.target,userBattlePacket);
    }
}
