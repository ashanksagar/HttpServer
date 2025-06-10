package com.ashanksagar.HttpServer.Routing;

import com.ashanksagar.HttpServer.http.HttpRequest;
import com.ashanksagar.HttpServer.http.HttpResponse;

public interface HttpHandler {
    HttpResponse handle(HttpRequest request);
}

