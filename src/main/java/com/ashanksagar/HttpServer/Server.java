package com.ashanksagar.HttpServer;

import com.ashanksagar.HttpServer.Routing.*;
import com.ashanksagar.HttpServer.config.Configuration;
import com.ashanksagar.HttpServer.config.JsonConfigHandler;
import com.ashanksagar.HttpServer.core.ServerListenerThread;
import com.ashanksagar.HttpServer.core.io.WebRootNotFoundException;
import org.slf4j.*;


import java.io.*;


public class Server {

    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        LOGGER.info("Server launched");

        JsonConfigHandler.getInstance().loadConfigFile("src/main/resources/http.json");
        Configuration conf = JsonConfigHandler.getInstance().getCurrentConfig();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using WebRoot: " + conf.getWebroot());

        Router router = new Router();
        router.addRoute("/hello", new HelloHandler());
        router.addRoute("/echo", new EchoHandler());
        router.addRoute("/upload", new UploadHandler());
        router.addRoute("/register", new RegisterHandler());


        ServerListenerThread serverListenerThread = null;
        try {
            serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot(), router);
            serverListenerThread.start();
            serverListenerThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
