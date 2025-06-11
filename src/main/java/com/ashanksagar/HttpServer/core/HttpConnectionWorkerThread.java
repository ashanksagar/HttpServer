package com.ashanksagar.HttpServer.core;

import com.ashanksagar.HttpServer.Routing.HttpHandler;
import com.ashanksagar.HttpServer.Routing.Router;
import com.ashanksagar.HttpServer.core.io.ReadFileException;
import com.ashanksagar.HttpServer.core.io.WebRootHandler;
import com.ashanksagar.HttpServer.core.io.WebRootNotFoundException;
import com.ashanksagar.HttpServer.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private final Socket socket;
    private final String webroot;
    private final Router router;

    public HttpConnectionWorkerThread(Socket socket, String webroot, Router router) {
        this.socket = socket;
        this.webroot = webroot;
        this.router = router;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()
        ) {
            HttpParser parser = new HttpParser();
            HttpRequest request;

            try {
                request = parser.parseHttpRequest(inputStream);
            } catch (HttpParsingException e) {
                LOGGER.warn("Bad HTTP request: {}", e.getMessage());
                sendSimpleResponse(outputStream, "400 Bad Request", "400 Bad Request");
                return;
            } catch (Exception e) {
                LOGGER.error("Unexpected parsing error", e);
                sendSimpleResponse(outputStream, "500 Internal Server Error", "500 Internal Server Error");
                return;
            }

            String target = request.getRequestTarget();
            if (target.equals("/")) target = "/index.html";

            try {
                HttpHandler handler = router.resolve(target);
                if (handler != null) {
                    HttpResponse response = handler.handle(request);
                    outputStream.write(response.getResponseBytes());
                    outputStream.flush();
                    return;
                }

                WebRootHandler staticHandler = new WebRootHandler(webroot);
                byte[] content = staticHandler.getFileByteArrayData(target);
                String mimeType = staticHandler.getFileMimeType(target);

                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n\r\n";

                outputStream.write(header.getBytes());
                outputStream.write(content);
                outputStream.flush();
            } catch (FileNotFoundException e) {
                sendSimpleResponse(outputStream, "404 Not Found", "404 Not Found");
            } catch (ReadFileException | WebRootNotFoundException e) {
                LOGGER.error("Web root error", e);
                sendSimpleResponse(outputStream, "500 Internal Server Error", "500 Internal Server Error");
            } catch (Exception e) {
                LOGGER.error("Fatal error during request handling", e);
                sendSimpleResponse(outputStream, "500 Internal Server Error", "500 Internal Server Error");
            }

        } catch (IOException e) {
            LOGGER.error("Socket I/O failure", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.warn("Socket close failed", e);
            }
        }
    }

    private void sendSimpleResponse(OutputStream outputStream, String status, String message) {
        try {
            String body = "<h1>" + message + "</h1>";
            String response = "HTTP/1.1 " + status + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Connection: close\r\n\r\n" + body;

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to send error response", e);
        }
    }
}
