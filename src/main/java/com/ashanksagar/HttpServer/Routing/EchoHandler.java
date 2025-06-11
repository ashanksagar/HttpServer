package com.ashanksagar.HttpServer.Routing;

import com.ashanksagar.HttpServer.http.*;

public class EchoHandler implements HttpHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod() != HttpMethod.POST) {
            String message = "Method not allowed";
            return new HttpResponse.Builder()
                    .httpVersion(request.getOriginalHttpVersion())
                    .statusCode(HttpStatusCodes.CLIENT_ERROR_401_METHOD_NOT_ALLOWED)
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(message.length()))
                    .addHeader("Connection", "close")
                    .messageBody(message.getBytes())
                    .build();
        }

        byte[] requestBody = request.getMessageBody();
        return new HttpResponse.Builder()
                .httpVersion(request.getOriginalHttpVersion())
                .statusCode(HttpStatusCodes.SUCCESS_200_OK)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Content-Length", String.valueOf(requestBody.length))
                .addHeader("Connection", "close")
                .messageBody(requestBody)
                .build();
    }
}

