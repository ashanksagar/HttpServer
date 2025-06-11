package com.ashanksagar.HttpServer.core;

import com.ashanksagar.HttpServer.Routing.Router;
import org.slf4j.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webRoot;
    private ServerSocket serverSocket;
    private Router router;

    private final ExecutorService threadPool;


    public ServerListenerThread(int port, String webRoot, Router router) throws IOException {
        this.port = port;
        this.webRoot = webRoot;
        this.serverSocket = new ServerSocket(this.port);
        this.router = router;
        this.threadPool = Executors.newFixedThreadPool(20);
    }



    @Override
    public void run() {
        try {

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                LOGGER.info("Connection Accepted: " + socket.getInetAddress());

                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, webRoot, router);
                threadPool.submit(workerThread);
            }


        } catch (IOException e) {
            LOGGER.error("Problem with setting socket", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            } if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        }
    }


}
