package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                sendText(exchange, gson.toJson(taskManager.getHistory()));
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        } catch (NotFoundException exception) {
            sendText(exchange, exception.getMessage());
        }
    }
}
