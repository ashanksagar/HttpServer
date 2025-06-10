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
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private final Socket socket;
    private final String webroot;
    private Router router;

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
            // Step 1: Parse the request
            HttpParser parser = new HttpParser();
            HttpRequest request;
            try {
                request = parser.parseHttpRequest(inputStream);
            } catch (HttpParsingException e) {
                LOGGER.error("Failed to parse HTTP request", e);
                sendSimpleResponse(outputStream, "400 Bad Request", "<h1>400 Bad Request</h1>");
                return;
            }

            String requestTarget = request.getRequestTarget();

            // Check for route match
            HttpHandler handler = router.resolve(requestTarget);
            if (handler != null) {
                HttpResponse response = handler.handle(request);
                outputStream.write(response.getResponseBytes());
                outputStream.flush();
                LOGGER.info("Routed and served: {}", requestTarget);
                return;
            }

            // Fall back to static file: add index.html if path ends with /
            if (requestTarget.equals("/")) {
                requestTarget += "index.html";
            }


            // Step 2: Serve the file
            try {
                WebRootHandler webRootHandler = new WebRootHandler(webroot);
                byte[] content = webRootHandler.getFileByteArrayData(requestTarget);
                String mimeType = webRootHandler.getFileMimeType(requestTarget);

                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "\r\n";

                outputStream.write(header.getBytes());
                outputStream.write(content);
                LOGGER.info("Served: {}", requestTarget);
            } catch (FileNotFoundException e) {
                sendSimpleResponse(outputStream, "404 Not Found", "<h1>404 Not Found</h1>");
                LOGGER.warn("File not found: {}", requestTarget);
            } catch (ReadFileException | WebRootNotFoundException e) {
                sendSimpleResponse(outputStream, "500 Internal Server Error", "<h1>500 Internal Server Error</h1>");
                LOGGER.error("Error serving file", e);
            }

            outputStream.flush();

        } catch (IOException e) {
            LOGGER.error("Communication error", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing socket", e);
            }
        }
    }

    private void sendSimpleResponse(OutputStream outputStream, String statusLine, String body) {
        try {
            String response = "HTTP/1.1 " + statusLine + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "\r\n" +
                    body;
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to send response: {}", statusLine, e);
        }
    }
}
