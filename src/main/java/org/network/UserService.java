package org.network;

import org.network.managers.BattleManager;
import org.network.managers.ChatManager;
import org.network.managers.GameServerManager;
import org.network.managers.LoginManager;
import org.network.packet.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserService extends Thread{
    public UserData userData = new UserData();
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;

    public UserService(Socket socket) {
        this.socket = socket;
        this.userData.id = AcceptServer.getUserVec().size();
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            ServerLogPanel.appendText("userService error");
        }

    }
    public byte[] MakePacket(String msg)
    {
        byte[] packet = new byte[AcceptServer.BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < AcceptServer.BUF_LEN; i++)
            packet[i] = 0;
        try {
            bb = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
        return packet;
    }
    public void Logout() {
        try {
            AcceptServer.removeUser(this);
            BattleManager.destroyRoomByUsername(userData.userName);
            oos.close();
            ois.close();
        } catch (IOException e) {
            //ignored
        }
        ServerLogPanel.appendText("Player " + (userData.userName != null ? userData.userName : "unknown(Login session)") + " disconnected.");
        if (userData.userName != null){
            ChatManager.sendServerLogToAll( userData.userName + "님이 게임에서 나가셨습니다.");
        }
        updateUserList();
        GameServerManager.requestLobbyUpdate();
    }
    public void run() {
        while (true) {

            try{
                Object obcm = null;
                String msg = null;

                if (socket == null){
                    ServerLogPanel.appendText("Socket closed");
                    break;
                }
                try {
                    obcm = ois.readObject();
                } catch (ClassNotFoundException e) {
                    ServerLogPanel.appendText("Packet broken");
                    e.printStackTrace();
                    return;
                }
                if (obcm == null){
                    //ServerLogPanel.appendText("Object null");
                    continue;
                }
                //오브젝트 입력 처리
                //로그인 입력 처리
                if (obcm instanceof LoginPacket loginPacket){
                    ServerLogPanel.appendText("===Login Task===");
                    LoginPacket responsePacket = new LoginPacket(-1, LoginPacketType.LOGIN_ACCEPT, loginPacket.username, "");
                    if (loginPacket.loginPacketType == LoginPacketType.LOGIN){
                        UserData result = LoginManager.checkUser(loginPacket);
                        if (result != null){
                            //로그인 성공 패킷 송신
                            responsePacket.id = result.id;
                            userData = result;
                            responsePacket.password = result.state;
                            if(result.state.equals("FirstIn")) {
                                ServerLogPanel.appendText(userData.userName + "님은 처음 들어오셨습니다. 포켓몬 선택창을 활성화 합니다.");}
                            else{
                                ChoosePocketPacket choosePocketPacket = new ChoosePocketPacket(userData.id, userData.userName, userData.pocketMonList);
                                sendObject(choosePocketPacket);
                            }
                            result.state = "Default";
                            sendObject(responsePacket);
                            updateUserList();
                            ChatManager.sendServerLogToAll( userData.userName + "님이 게임에 들어오셨습니다.");
                            GameServerManager.requestLobbyUpdate();
                        }
                        else {
                            //로그인 실패 패킷 송신
                            responsePacket.loginPacketType = LoginPacketType.LOGIN_REFUSE;
                            sendObject(responsePacket);
                        }
                    }
                    if (loginPacket.loginPacketType==LoginPacketType.SIGN_IN){
                        if (LoginManager.createUser(loginPacket)){
                            //회원가입 성공 패킷 송신
                            responsePacket.loginPacketType = LoginPacketType.SIGN_IN_ACCEPT;
                            sendObject(responsePacket);
                        }
                        else {
                            //회원가입 실패 패킷 송신
                            responsePacket.loginPacketType = LoginPacketType.SIGN_IN_REFUSE;
                            sendObject(responsePacket);
                        }
                    }
                    continue;
                }
                //채팅 입력 처리
                if (obcm instanceof UserChatPacket userChatPacket){
                    ChatManager.sendMessage(userChatPacket);
                    continue;
                }
                if (obcm instanceof UserMovePacket userMovePacket){
                    //ServerLogPanel.appendText("Receive move packet" + userMovePacket.direction.x+"/" + userMovePacket.direction.y);
                    userData.currentPos.x += userMovePacket.direction.x;
                    userData.currentPos.y += userMovePacket.direction.y;
                    if (userMovePacket.direction.x >= 1){
                        userData.seeDirection = 1;
                    }
                    else if (userMovePacket.direction.x <= -1){
                        userData.seeDirection = 3;
                    }
                    if (userMovePacket.direction.y >= 1){
                        userData.seeDirection = 2;
                    }
                    else if (userMovePacket.direction.y <= -1){
                        userData.seeDirection = 0;
                    }
                    GameServerManager.requestLobbyUpdate();//게임 관리자에게 자신이 업데이트 되었음을 알림
                }
                //전투 패킷 수신 파트
                if (obcm instanceof UserBattlePacket userBattlePacket){
                    ServerLogPanel.appendText("Receive battle packet from "+ userBattlePacket.username + " to target " + userBattlePacket.target + " by " + userBattlePacket.commandType);
                    BattleManager.handleBattlePacket(userBattlePacket);
                }
                if (obcm instanceof ChoosePocketPacket choosePocketPacket){
                    userData.pocketMonList = choosePocketPacket.pocketMonList;
                    ServerLogPanel.appendText(choosePocketPacket.pocketMonList.toString());
                }
            }
            catch (Exception exception){
                exception.printStackTrace();
                ServerLogPanel.appendText(userData.userName + " Error occured disconnect");
                Logout();
                return;
            }
        }
    }
    private void updateUserList(){
        UserListPacket userListPacket = new UserListPacket(
                -1,
                "Server",
                AcceptServer.getUsernameList()
        );
        AcceptServer.sendObjectToAll(userListPacket);
    }
    public void SendMsg(UserChatPacket userChatPacket) {
        ServerLogPanel.appendText("Send");
        sendObject(userChatPacket);
    }
    public void SendMsg(int id,String username,String str,String target){
        UserChatPacket userChatPacket = new UserChatPacket(id, username, str, target);
        sendObject(userChatPacket);
    }
    public void sendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            //ServerLogPanel.appendText("Send to " + userData.userName);
            oos.writeObject(ob);
            //oos.writeObject(null);
        } catch (IOException e) {
            ServerLogPanel.appendText("SendObject Error");
        }
    }
}
