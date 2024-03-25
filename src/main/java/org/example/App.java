package org.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    public static void main( String[] args )
    {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(1234), 0);
            server.createContext("/", new MyHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server started!");
        } catch (IOException e) {
            System.err.println("Errore durante l'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
