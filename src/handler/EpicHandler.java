package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendError(exchange, 405, "Метод не поддерживается");
            }

        } catch (NotFoundException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (TaskOverlapException e) {
            sendError(exchange, 409, e.getMessage());
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "Некорректный ID");
        } catch (Exception e) {
            sendError(exchange, 500, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 2) {
            sendText(exchange, gson.toJson(taskManager.getEpicList()));
        } else if (path.length == 3) {
            int id = Integer.parseInt(path[2]);
            Epic epic = taskManager.getEpicById(id);
            sendText(exchange, gson.toJson(epic));
        } else {
            sendError(exchange, 400, "Неверный путь для GET");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(requestBody, Epic.class);

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
        } else {
            taskManager.updateEpic(epic);
        }
        sendText(exchange, gson.toJson(epic));
    }

    private void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            int id = Integer.parseInt(path[2]);
            taskManager.removeEpic(id);
            sendText(exchange, "{\"message\": \"Задача удалена!\"}");
        } else {
            sendError(exchange, 400, "Неверный путь для DELETE");
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
