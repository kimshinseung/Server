package org.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class UserService extends Thread{
    private UserData userData = new UserData();
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;

    public UserService(Socket socket) {
        this.socket = socket;
        this.userData.id = AcceptServer.getUserVec().size();
        try {
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            byte[] b = new byte[AcceptServer.BUF_LEN];
            dis.read(b);
            String line1 = new String(b);
            String[] msg = line1.split(" ");
            userData.userName = msg[1].trim();
            userData.isSleep = false;
            Login();
        } catch (Exception e) {
            System.out.println("userService error");
        }

    }
    public void Login() {
        System.out.println("["+userData.userName+"] is login to server.");
        WriteOne("Welcome to Java chat server\n");
        WriteOne(userData.userName + "\n");
        String msg ="["+userData.userName+"] has entered the server.\n";
        WriteAll(msg);
    }

    public void Logout()
    {
        String msg ="["+userData.userName+"] has exited the server.\n";
        AcceptServer.getUserVec().removeElement(this);
        WriteAll(msg);
        System.out.println("[" + userData.userName + "] has exited. Remaining member number : " + AcceptServer.getUserVec().size());
    }
    public void WriteOne(String msg) {
        try {
            byte[] bb;
            bb = MakePacket(msg);
            System.out.println(msg);
            dos.write(bb, 0, bb.length);
        } catch (IOException e) {
            System.out.println("dos.write() error");
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Logout();
        }
    }

    public void WriteAll(String str)
    {
        Vector<UserService> userVec = AcceptServer.getUserVec();

        for (int i = 0; i < userVec.size(); i++) {
            UserService user = userVec.elementAt(i);
            if (!user.userData.isSleep) {
                System.out.println("Send message to all member. Target name : " + user.userData.userName);
                user.WriteOne(str);
            }
        }
    }
    public void WriteOthers(String str)
    {
        Vector<UserService> userVec = AcceptServer.getUserVec();
        for (int i = 0; i < userVec.size(); i++) {
            UserService user = userVec.elementAt(i);
            if (user!=this && !user.userData.isSleep)
                user.WriteOne(str);
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

    public void run() {
        while (true) {
            try {
                // String msg = dis.readUTF();
                byte[] b = new byte[AcceptServer.BUF_LEN];
                int ret;
                ret = dis.read(b);
                if (ret < 0) {
                    System.out.println("dis.read() < 0 error");
                    try {
                        dos.close();
                        dis.close();
                        socket.close();
                        Logout();
                        break;
                    } catch (Exception ee) {
                        break;
                    } // catch�� ��
                }
                String msg = new String(b,  "euc-kr");
                msg = msg.trim();
                System.out.println(msg);

                String[] args = msg.split(" ");
                if (args.length == 1) {
                    userData.isSleep = false;
                } else if (args[1].matches("/exit")) {
                    Logout();
                    break;
                } else if (args[1].matches("/list")) {
                    WriteOne("User list\n");
                    WriteOne("Name\tStatus\n");
                    WriteOne("-----------------------------\n");
                    Vector<UserService> userVec = AcceptServer.getUserVec();
                    for (int i = 0; i < userVec.size(); i++) {
                        UserService user = userVec.elementAt(i);
                        WriteOne(user.userData.userName + "\t" + (user.userData.isSleep?"S":"O") + "\n");
                    }
                    WriteOne("-----------------------------\n");
                } else if (args[1].matches("/sleep")) {
                    userData.isSleep = true;
                } else if (args[1].matches("/wakeup")) {
                    userData.isSleep = false;;
                } else if (args[1].matches("/to")) {
                    Vector<UserService> userVec = AcceptServer.getUserVec();
                    for (int i = 0; i < userVec.size(); i++) {
                        UserService user = (UserService) userVec.elementAt(i);
                        if (user.userData.userName.equals(args[2]) && !user.userData.isSleep) {
                            String msg2 = "";
                            for (int j = 3;j<args.length;j++) {// ���� message �κ�
                                msg2 += args[j];
                                if (j < args.length - 1)
                                    msg2 += " ";
                            }
                            user.WriteOne("[message]" + args[0] + " " + msg2 + "\n");
                            break;
                        }
                    }
                } else {
                    userData.isSleep = false;
                    WriteAll(msg + "\n"); // Write All
                }
            } catch (IOException e) {
                System.out.println("dis.read() error");
                try {
                    dos.close();
                    dis.close();
                    socket.close();
                    Logout();
                    break;
                } catch (Exception ee) {
                    break;
                }
            }
        }
    }
}
