package org.network;

import org.network.data.PocketMonData;
import org.network.managers.GameServerManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Main {
    private ServerSocket socket;

    public static void main(String[] args)
    {
        PocketMonData.InitPocketmonData();
        AcceptServer acceptServer = new AcceptServer();
        GameServerManager.current.start();
        ServerLogPanel serverLogPanel = new ServerLogPanel();
        serverLogPanel.setStartEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptServer.createServer();
                acceptServer.start();
            }
        });
    }
}