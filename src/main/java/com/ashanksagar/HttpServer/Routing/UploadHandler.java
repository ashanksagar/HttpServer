package com.ashanksagar.HttpServer.Routing;

import com.ashanksagar.HttpServer.http.*;

import java.io.IOException;
import java.nio.file.*;

public class UploadHandler implements HttpHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod() != HttpMethod.PUT) {
            String msg = "Method Not Allowed";
            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.CLIENT_ERROR_401_METHOD_NOT_ALLOWED)
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(msg.length()))
                    .addHeader("Connection", "close")
                    .messageBody(msg.getBytes())
                    .build();
        }

        byte[] data = request.getMessageBody();

        try {
            Files.write(Paths.get("uploaded.txt"), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            String error = "Failed to save file.";
            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(error.length()))
                    .addHeader("Connection", "close")
                    .messageBody(error.getBytes())
                    .build();
        }

        String success = "File uploaded successfully.";
        return new HttpResponse.Builder()
                .httpVersion(request.getOriginalHttpVersion())
                .statusCode(HttpStatusCodes.SUCCESS_200_OK)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Content-Length", String.valueOf(success.length()))
                .addHeader("Connection", "close")
                .messageBody(success.getBytes())
                .build();
    }
}

