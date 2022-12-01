package org.network.data;

import org.network.ServerLogPanel;
import org.network.UserData;
import org.network.managers.LoginManager;
import org.network.pocketmon.PocketMonster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleData {
    public int roomId = -1;
    public Map<String,String> playerState = new HashMap<>();
    public Map<String,Integer> currentPocketMon = new HashMap<>();
    public Map<String, List<BattlePocketData>> playerPocketMonList = new HashMap<>();
    public BattleData(List<String> player){
        for (String username : player){
            playerState.put(username,"SELECT");
            UserData user = LoginManager.getUserDataByUsername(username);
            //ServerLogPanel.appendText(user==null?"null":"exist");
            List<BattlePocketData> pocketMonsterList = new ArrayList<>();
            ServerLogPanel.appendText(user.pocketMonList.toString());
            for (int pocketId : user.pocketMonList){
                PocketMonster pocket = PocketMonData.monsterInfo.get(pocketId);
                BattlePocketData battlePocketData = new BattlePocketData();
                battlePocketData.pocketId = pocketId;
                battlePocketData.isDead = false;
                battlePocketData.currentHealth = pocket.getCurrent_HP();
                battlePocketData.maxHealth = pocket.getMax_HP();
                pocketMonsterList.add(battlePocketData);
            }
            playerPocketMonList.put(username,pocketMonsterList);
        }
    }
    public int selectPocketMonByIndex(String username,int index){
        //ServerLogPanel.appendText(username + "/ Input index : " + index);
        if (playerPocketMonList.get(username).get(index).isDead){
            return -1;
        }
        currentPocketMon.replace(username,index);
        return playerPocketMonList.get(username).get(index).pocketId;
    }
}
