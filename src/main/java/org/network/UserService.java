package org.network;

import org.network.managers.ChatManager;
import org.network.managers.LoginManager;
import org.network.packet.LoginPacket;
import org.network.packet.LoginPacketType;
import org.network.packet.UserChatPacket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

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
        AcceptServer.getUserVec().removeElement(this);

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
                if (obcm instanceof LoginPacket loginPacket){
                    ServerLogPanel.appendText("===Login Task===");
                    if (loginPacket.loginPacketType == LoginPacketType.LOGIN){
                        LoginManager.checkUser(loginPacket);
                    }
                    if (loginPacket.loginPacketType==LoginPacketType.SIGN_IN){
                        LoginManager.createUser(loginPacket);
                    }
                    continue;
                }
                if (obcm instanceof UserChatPacket userChatPacket){
                    ChatManager.sendMessage(userChatPacket);
                    continue;
                }
            }
            catch (Exception exception){

            }
        }
    }
    public void SendMsg(UserChatPacket userChatPacket) {
        SendObject(userChatPacket);
    }
    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {
            ServerLogPanel.appendText("SendObject Error");
        }
    }
}
