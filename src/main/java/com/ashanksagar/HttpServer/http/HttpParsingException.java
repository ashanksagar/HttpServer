package com.ashanksagar.HttpServer.http;

public class HttpParsingException extends Exception {

    private HttpStatusCodes error;

    public HttpParsingException(HttpStatusCodes error) {
        super(error.MESSAGE);
        this.error = error;
    }

    public HttpStatusCodes getError() {
        return this.error;
    }
}
