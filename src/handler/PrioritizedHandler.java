package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            if (method.equals("GET")) {
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, exception.getMessage());
        }
    }
}
