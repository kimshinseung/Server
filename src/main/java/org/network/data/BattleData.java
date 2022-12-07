package org.network.data;

import org.network.ServerLogPanel;
import org.network.UserData;
import org.network.managers.BattleManager;
import org.network.managers.LoginManager;
import org.network.packet.UserBattlePacket;
import org.network.pocketmon.PocketMonster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleData {
    public int roomId = -1;
    public String currentTurnUsername = "";
    public Map<String,String> playerState = new HashMap<>();
    public Map<String,Integer> currentPocketMon = new HashMap<>();
    public Map<String, List<BattlePocketData>> playerPocketMonList = new HashMap<>();
    //public Map<String, UserBattlePacket> playerCommand = new HashMap<>();
    public BattleData(List<String> player){
        for (String username : player){
            playerState.put(username,"SELECT");
            UserData user = LoginManager.getUserDataByUsername(username);
            //ServerLogPanel.appendText(user==null?"null":"exist");
            List<BattlePocketData> pocketMonsterList = new ArrayList<>();
            //ServerLogPanel.appendText(user.pocketMonList.toString());
            for (int pocketId : user.pocketMonList){
                PocketMonster pocket = PocketMonData.monsterInfo.get(pocketId);
                BattlePocketData battlePocketData = new BattlePocketData();
                battlePocketData.pocketId = pocketId;
                battlePocketData.isDead = false;
                battlePocketData.setCurrentHealth(pocket.getCurrent_HP());
                battlePocketData.setMaxHealth(pocket.getMax_HP());
                pocketMonsterList.add(battlePocketData);
            }
            currentPocketMon.put(username,0);
//            playerCommand.put(username,null);
            playerPocketMonList.put(username,pocketMonsterList);
        }
        currentTurnUsername = player.get(0);
    }
    public int selectPocketMonByIndex(String username,int index){
        //ServerLogPanel.appendText(username + "/ Input index : " + index);
        if (playerPocketMonList.get(username).get(index).isDead){
            return -1;
        }
        currentPocketMon.replace(username,index);
        return playerPocketMonList.get(username).get(index).pocketId;
    }

    public void giveDamageToCurrentPocketMon(UserBattlePacket userBattlePacket) {
        if (userBattlePacket.target.equals("OPPONENT")){
            String currentTarget = "";
            for (String username : playerState.keySet()){
                if (!username.equals(userBattlePacket.username)){
                    currentTarget = username;
                    break;
                }
            }
            if (currentTarget.isBlank()){
                ServerLogPanel.appendText("Attack error. current target : " + currentTarget);
                return;
            }
            //ServerLogPanel.appendText(currentPocketMon.keySet().toString() + " / " + currentTarget);
            int opponentPocketId = currentPocketMon.get(currentTarget);
            int myPocketId = currentPocketMon.get(userBattlePacket.username);
            BattlePocketData battlePocketData = playerPocketMonList.get(currentTarget).get(opponentPocketId);
            int attackType = userBattlePacket.args.get(0);
            if (attackType == -1) {
                battlePocketData.giveDamage(PocketMonData.monsterInfo.get(
                                playerPocketMonList.get(userBattlePacket.username).get(myPocketId).pocketId).getAtk());
            } else {
                battlePocketData.giveDamage(PocketMonData.monsterInfo.get(
                                        playerPocketMonList.get(userBattlePacket.username).get(myPocketId).pocketId)
                                .getSkill_list()[attackType].getPower());
            }
        }
    }
    public BattlePocketData getCurrentPocketDataByUsername(String username){
        int index = currentPocketMon.get(username);
        //ServerLogPanel.appendText(username + "/"+index);
        return playerPocketMonList.get(username).get(index);
    }
    public String getOpponent(String username){
        for (String us : playerState.keySet()){
            if (!us.equals(username)) return us;
        }
        ServerLogPanel.appendText("Error attack target not found");
        return null;
    }

    public void giveHealToCurrentPocketMon(UserBattlePacket userBattlePacket) {
        if (userBattlePacket.target.equals("ME")){
            int myPocketId = currentPocketMon.get(userBattlePacket.username);
            BattlePocketData battlePocketData = playerPocketMonList.get(userBattlePacket.username).get(myPocketId);
            int healType = userBattlePacket.args.get(0);
            switch (healType) {
                case 0 -> battlePocketData.giveHeal(30);
                case 1 -> battlePocketData.giveHeal(50);
            }
        }
    }
//    public boolean addBattleProgress(UserBattlePacket userBattlePacket) {
//        playerCommand.put(userBattlePacket.username, userBattlePacket);
//        for (UserBattlePacket ubp : playerCommand.values()) {
//            if (ubp == null) {
//                return false;
//            }
//        }
//        return true;
//    }
    public boolean checkPlayerDefeat(String username)
    {
        for (BattlePocketData pocketData : playerPocketMonList.get(username)){
            if (!pocketData.isDead) return false;
        }
        return true;
    }

    public boolean checkPlayerCurrentPocketDefeat(String username) {
        return playerPocketMonList.get(username).get(currentPocketMon.get(username)).isDead;
    }

    public List<Integer> getPlayerRemainPocketMon(String username) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < playerPocketMonList.get(username).size();i++){
            if (playerPocketMonList.get(username).get(i).isDead) continue;
            result.add(i);
        }
        return result;
    }
    public String swapTurn(){
        String opponentName = getOpponent(currentTurnUsername);
        currentTurnUsername = opponentName;
        return opponentName;
    }
}
