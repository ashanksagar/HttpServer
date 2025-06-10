package com.ashanksagar.HttpServer.config;

import com.ashanksagar.HttpServer.util.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonConfigHandler {

    private static JsonConfigHandler myJsonConfigHandler;
    private static Configuration myCurrentConfig;

    private JsonConfigHandler() {
    }

    public static JsonConfigHandler getInstance() {
        if (myJsonConfigHandler == null)
            myJsonConfigHandler = new JsonConfigHandler();
        return myJsonConfigHandler;
    }

    public void loadConfigFile(String filePath) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException(e);
        }
        StringBuffer sb = new StringBuffer();
        int i;
        try {
            while ((i = fileReader.read()) != -1) {
                sb.append((char)i);
            }
        } catch (IOException e) {
             throw new HttpConfigurationException(e);
        }
        JsonNode conf = null;
        try {
            conf = Json.parse(sb.toString());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error parsing Config File", e);
        }
        try {
            myCurrentConfig = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing Config File (Internal)", e);
        }
    }

    public Configuration getCurrentConfig() {
        if (myCurrentConfig == null) {
            throw new HttpConfigurationException("Current Configuration not Found");
        }
        return myCurrentConfig;
    }
}
