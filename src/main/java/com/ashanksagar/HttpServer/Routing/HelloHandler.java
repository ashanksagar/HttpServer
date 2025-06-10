package com.ashanksagar.HttpServer.Routing;

import com.ashanksagar.HttpServer.http.*;

public class HelloHandler implements HttpHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        String body = "<h1>Hello, world!</h1>";
        return new HttpResponse.Builder()
                .httpVersion(request.getOriginalHttpVersion())
                .statusCode(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST) // or 200 OK if you define it
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(body.length()))
                .messageBody(body.getBytes())
                .build();
    }
}

