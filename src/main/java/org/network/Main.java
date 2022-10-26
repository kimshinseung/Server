package org.network;

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
        AcceptServer acceptServer = new AcceptServer(30000);
        acceptServer.start();
    }
}