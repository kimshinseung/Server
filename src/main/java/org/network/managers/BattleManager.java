package org.network.managers;

import org.network.AcceptServer;
import org.network.ServerLogPanel;
import org.network.data.BattleData;
import org.network.data.BattlePocketData;
import org.network.data.PocketMonData;
import org.network.packet.UserBattlePacket;
import org.network.packet.UserChatPacket;
import org.network.pocketmon.PocketMonster;

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
        if (battleData.battleState.equals("END")){
            String user1 = userBattlePacket.username;
            String user2 = battleData.getOpponent(user1);
            int currentRoom = roomMap.get(user1);
            roomMap.remove(user2);
            roomMap.remove(user1);
            roomBattleData.remove(currentRoom);
            ServerLogPanel.appendText("["+user1+","+user2+"] room battle end. delete room.");
            return;
        }
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
        //sendBattleLogToUsername(turnUsername,"당신의 턴 입니다.");
        userBattlePacket.target = waitUsername;
        userBattlePacket.commandType = "WAIT";
        AcceptServer.sendObjectByUsername(waitUsername,userBattlePacket);
        //sendBattleLogToUsername(waitUsername,"상대의 턴 입니다.");
    }
    private static void sendBattleLogToUsername(String username,String log){
        UserBattlePacket userBattlePacket = new UserBattlePacket(
                -1,
                "SERVER",
                "BATTLE_LOG",
                log,
                new ArrayList<>()
        );//로그 타입의 패킷일 때는 타겟을 배틀 로그로 사용
        AcceptServer.sendObjectByUsername(username,userBattlePacket);
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

        AcceptServer.sendObjectByUsername(userBattlePacket.username,battleLog);
        AcceptServer.sendObjectByUsername(userBattlePacket.username,userBattlePacket);

        String opponent = battleData.getOpponent(userBattlePacket.username);
        AcceptServer.sendObjectByUsername(opponent,battleLog);
        AcceptServer.sendObjectByUsername(opponent,userBattlePacket);

        int pocketId = battleData.getCurrentPocketDataByUsername(userBattlePacket.username).pocketId;
        String msg = userBattlePacket.username + "님이<br>" +
                PocketMonData.monsterInfo.get(pocketId).getName()+"으로<br>포켓몬을 교체하셨습니다.";
        sendBattleLogToUsername(userBattlePacket.username, msg);
        sendBattleLogToUsername(opponent,msg);

        battleData.playerState.replace(userBattlePacket.username,"COMMAND");
        updatePocketMonHealth(battleData);
    }

    private static void userItemToTarget(UserBattlePacket userBattlePacket) {
        ServerLogPanel.appendText(userBattlePacket.username + " used item that " + userBattlePacket.args.get(0));
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        battleData.giveHealToCurrentPocketMon(userBattlePacket);
        String msg = userBattlePacket.username + "님이<br>" + (userBattlePacket.args.get(0)==0?"라즈베리열매":"베리베리열매") + "를<br>사용하셨습니다.";
        sendBattleLogToUsername(userBattlePacket.username, msg);
        sendBattleLogToUsername(battleData.getOpponent(userBattlePacket.username),msg);
        updatePocketMonHealth(battleData);
    }

    private static void attackToTarget(UserBattlePacket userBattlePacket) {
        int usedSkillNum = userBattlePacket.args.get(0);
        ServerLogPanel.appendText(userBattlePacket.username + " attack by index : " + usedSkillNum);
        int roomId = roomMap.get(userBattlePacket.username);
        BattleData battleData = roomBattleData.get(roomId);
        battleData.giveDamageToCurrentPocketMon(userBattlePacket);
        PocketMonster pocketMon = PocketMonData.monsterInfo.get(battleData.getCurrentPocketDataByUsername(userBattlePacket.username).pocketId);
        String msg = userBattlePacket.username + "님의 " + pocketMon.getName()+"(이)가<br>" +
                (usedSkillNum==-1?"기본공격":pocketMon.getSkill_list()[usedSkillNum].getName())+
                "을 사용하셨습니다.";
        sendBattleLogToUsername(userBattlePacket.username, msg);
        sendBattleLogToUsername(battleData.getOpponent(userBattlePacket.username),msg);
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
            String opponent = battleData.getOpponent(username);
            AcceptServer.sendObjectByUsername(username,userBattlePacket1);
            AcceptServer.sendObjectByUsername(opponent,userBattlePacket1);
            String msg = opponent + "님이 " + username+"님에게 승리하였습니다.";
            UserChatPacket userChatPacket = new UserChatPacket(
                    -1,
                    "SERVER",
                    msg,
                    "-ALL-"
            );
            AcceptServer.sendObjectToAll(userChatPacket);
            ServerLogPanel.appendText("");
            battleData.battleState = "END";
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

    public static void destroyRoomByUsername(String username) {
        int roomId = roomMap.get(username);
        BattleData battleData = roomBattleData.get(roomId);
        String user2 = battleData.getOpponent(username);
        int currentRoom = roomMap.get(username);
        roomMap.remove(user2);
        roomMap.remove(username);
        roomBattleData.remove(currentRoom);
        ServerLogPanel.appendText("["+username+","+user2+"] room battle end. delete room.");
        UserBattlePacket userBattlePacket= new UserBattlePacket(-1,"SERVER","EXIT","ALL",new ArrayList<>());
        AcceptServer.sendObjectByUsername(username,userBattlePacket);
        AcceptServer.sendObjectByUsername(user2,userBattlePacket);
    }
}
