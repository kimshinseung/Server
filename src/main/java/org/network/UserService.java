package org.network;

import org.network.managers.ChatManager;
import org.network.managers.LoginManager;
import org.network.packet.LoginPacket;
import org.network.packet.LoginPacketType;
import org.network.packet.UserChatPacket;
import org.network.packet.UserListPacket;

import java.io.*;
import java.net.Socket;

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
            oos.close();
            ois.close();
        } catch (IOException e) {
            //ignored
        }
        ServerLogPanel.appendText("Player " + (userData.userName != null ? userData.userName : "unknown(Login session)") + " disconnected.");
        updateUserList();
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
                    ServerLogPanel.appendText("Object null");
                    break;
                }
                //오브젝트 입력 처리
                //로그인 입력 처리
                if (obcm instanceof LoginPacket loginPacket){
                    ServerLogPanel.appendText("===Login Task===");
                    LoginPacket responsePacket = new LoginPacket(-1, LoginPacketType.LOGIN_ACCEPT, loginPacket.username, "");
                    if (loginPacket.loginPacketType == LoginPacketType.LOGIN){
                        int result = LoginManager.checkUser(loginPacket);
                        if (result >= 0){
                            //로그인 성공 패킷 송신
                            responsePacket.id = result;
                            userData.userName = loginPacket.username;
                            sendObject(responsePacket);
                            updateUserList();
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
            }
            catch (Exception exception){
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
            oos.writeObject(ob);
            ServerLogPanel.appendText("Send end");
        } catch (IOException e) {
            ServerLogPanel.appendText("SendObject Error");
        }
    }
}
