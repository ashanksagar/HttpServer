package com.ashanksagar.HttpServer.Routing;


import com.ashanksagar.HttpServer.http.*;
import com.ashanksagar.HttpServer.database.SimpleDatabase;
import com.ashanksagar.HttpServer.util.Json;
import com.fasterxml.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;

public class RegisterHandler implements HttpHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod() != HttpMethod.POST) {
            String error = "Method Not Allowed";
            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.CLIENT_ERROR_401_METHOD_NOT_ALLOWED)
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(error.length()))
                    .addHeader("Connection", "close")
                    .messageBody(error.getBytes())
                    .build();
        }

        try {
            String jsonBody = new String(request.getMessageBody(), StandardCharsets.UTF_8);
            JsonNode user = Json.parse(jsonBody);

            // Basic validation
            if (!user.has("username") || !user.has("email")) {
                String msg = "Missing required fields";
                return new HttpResponse.Builder()
                        .httpVersion(request.getOriginalHttpVersion())
                        .statusCode(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST)
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("Content-Length", String.valueOf(msg.length()))
                        .addHeader("Connection", "close")
                        .messageBody(msg.getBytes())
                        .build();
            }

            SimpleDatabase.registeredUsers.add(user);

            String responseJson = String.format("{\"status\":\"ok\",\"user\":\"%s\"}", user.get("username").asText());
            byte[] body = responseJson.getBytes(StandardCharsets.UTF_8);

            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.SUCCESS_200_OK)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Content-Length", String.valueOf(body.length))
                    .addHeader("Connection", "close")
                    .messageBody(body)
                    .build();

        } catch (Exception e) {
            String error = "Invalid JSON";
            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST)
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(error.length()))
                    .addHeader("Connection", "close")
                    .messageBody(error.getBytes())
                    .build();
        }
    }
}

