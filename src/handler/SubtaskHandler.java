package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    if (path.length == 2) {
                        sendText(exchange, gson.toJson(taskManager.getSubtasksList()));
                    } else if (path.length == 3) {
                        int id = Integer.parseInt(path[2]);
                        Subtask subtask = taskManager.getSubtaskById(id);
                        sendText(exchange, gson.toJson(subtask));
                    }
                    break;
                case "POST":
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                    if (subtask.getId() == 0) {
                        taskManager.addSubtask(subtask);
                    } else {
                        taskManager.updateSubtask(subtask);
                    }
                    sendText(exchange, gson.toJson(subtask));
                    break;
                case "DELETE":
                    int id = Integer.parseInt(path[2]);
                    taskManager.removeSubtask(id);
                    sendText(exchange, "{\"message\": \"Задача удалена!\"}");
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                    break;
            }

        } catch (NotFoundException exception) {
            sendNotFound(exchange, exception.getMessage());
        } catch (TaskOverlapException exception) {
            sendHasInteractions(exchange, exception.getMessage());
        }
    }
}
