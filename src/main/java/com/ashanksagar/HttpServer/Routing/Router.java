package com.ashanksagar.HttpServer.Routing;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, HttpHandler> routes = new HashMap<>();

    public void addRoute(String path, HttpHandler handler) {
        routes.put(path, handler);
    }

    public HttpHandler resolve(String path) {
        return routes.get(path);
    }
}

