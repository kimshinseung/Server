package org.network.managers;

import org.network.AcceptServer;
import org.network.UserService;
import org.network.data.UserMoveData;
import org.network.packet.UserMoveListPacket;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameServerManager extends Thread{
    public static final GameServerManager current = new GameServerManager();
    private static int serverFrame = 17;
    private long preTime;//루프 간격을 조절하기 위한 시간 체크값
    private static int remainLobbyUpdate = 2;



    @Override
    public void run() {
        while (true){
            preTime=System.currentTimeMillis();
            sendUserPosPacket();
            if (remainLobbyUpdate > 0){

                remainLobbyUpdate--;
            }

            if(System.currentTimeMillis()-preTime<serverFrame) {
                try {
                    Thread.sleep(serverFrame-System.currentTimeMillis()+preTime);
                } catch (InterruptedException e) {
                    //ignored
                }
            }
        }
    }
    private void sendUserPosPacket() {
        //System.out.println("User post packet" + adfdf++);
        Vector<UserService> userVec = AcceptServer.getUserVec();
        List<UserMoveData> userMoveList = new ArrayList<>();
        for (UserService user : userVec){
            if (user.userData.userName == null) continue;
            userMoveList.add(new UserMoveData(user.userData.userName,user.userData.seeDirection,new Point(user.userData.currentPos)));
        }
        UserMoveListPacket userMoveListPacket = new UserMoveListPacket(
                -1,
                "Server",
                userMoveList
        );
        AcceptServer.sendObjectToAll(userMoveListPacket);
    }
    public static void requestLobbyUpdate() {
        remainLobbyUpdate = 2;
    }
}
