package httpServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private HttpClient client;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 12, 0));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Task> tasksFromManager = manager.getTasksList();
        assertNotNull(tasksFromManager, "Список задач не должен быть null");
        assertEquals(1, tasksFromManager.size(), "Должна быть одна задача");
        assertEquals("Test Task", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getTitle(), responseTask.getTitle(), "Название задачи в ответе не совпадает");
        assertEquals(task.getDescription(), responseTask.getDescription(), "Описание задачи в ответе не совпадает");
        assertEquals(TaskStatus.NEW, responseTask.getStatus(), "Статус задачи должен быть NEW");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 12, 0));
        manager.addTask(task);

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask, "Задача не должна быть null");
        assertEquals(task.getId(), responseTask.getId(), "ID задачи не совпадает");
        assertEquals("Test Task", responseTask.getTitle(), "Название задачи не совпадает");
    }

    @Test
    public void testGetTaskByNonExistentId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа должен быть 404 для несуществующей задачи");
        assertTrue(response.body().contains("Задача с id: 999, не существует в менеджере!"),
                "Ответ должен содержать сообщение о NotFoundException");
    }

    @Test
    public void testRemoveTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 12, 0));
        manager.addTask(task);

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertTrue(response.body().contains("Задача удалена"), "Ответ должен содержать сообщение об удалении");

        List<Task> tasksFromManager = manager.getTasksList();
        assertTrue(tasksFromManager.isEmpty(), "Список задач должен быть пуст после удаления");
    }

    @Test
    public void testRemoveNonExistentTask() throws IOException, InterruptedException {

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа должен быть 404 для несуществующей задачи");
        assertTrue(response.body().contains("Удаляемая задача не существует!"),
                "Ответ должен содержать сообщение о NotFoundException");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc 1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 12, 0));
        Task task2 = new Task("Task 2", "Desc 2", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 8, 30, 13, 0));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertNotNull(tasks, "Список задач не должен быть null");
        assertEquals(2, tasks.size(), "Должно быть две задачи");
        assertEquals("Task 1", tasks.get(0).getTitle(), "Название первой задачи не совпадает");
        assertEquals("Task 2", tasks.get(1).getTitle(), "Название второй задачи не совпадает");
    }
}