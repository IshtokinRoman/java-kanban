package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public abstract class BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] textToBytes = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        int rCode = 200;
        httpExchange.sendResponseHeaders(rCode, textToBytes.length);
        httpExchange.getResponseBody().write(textToBytes);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] textToBytes = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        int rCode = 404;
        httpExchange.sendResponseHeaders(rCode, textToBytes.length);
        httpExchange.getResponseBody().write(textToBytes);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] textToBytes = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        int rCode = 406;
        httpExchange.sendResponseHeaders(rCode, textToBytes.length);
        httpExchange.getResponseBody().write(textToBytes);
        httpExchange.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}