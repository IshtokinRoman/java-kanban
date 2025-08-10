package httpServer;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest {

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 12, 0));
        taskManager.addTask(task);
        Epic epic = new Epic("EpicTest", "desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("SubtaskTitle", "desc", epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        server.start();

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        URI url = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> history = taskManager.getHistory();
        Task expectedTask = history.getFirst();

        assertEquals(200, response.statusCode(), "Код должен быть 200");
        assertEquals(3, history.size(), "Должно быть 3 задачи");
        assertEquals(expectedTask, task, "Задачи должны совпадать");
        server.stop();
    }
}
