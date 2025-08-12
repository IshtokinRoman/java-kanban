package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
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
                        sendText(exchange, gson.toJson(taskManager.getTasksList()));
                    } else if (path.length == 3) {
                        int id = Integer.parseInt(path[2]);
                        Task task = taskManager.getTaskById(id);
                        sendText(exchange, gson.toJson(task));
                    }
                    break;
                case "POST":
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(requestBody, Task.class);
                    if (task.getId() == 0) {
                        taskManager.addTask(task);
                    } else {
                        taskManager.updateTask(task);
                    }
                    sendText(exchange, gson.toJson(task));
                    break;
                case "DELETE":
                    int id = Integer.parseInt(path[2]);
                    taskManager.removeTask(id);
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