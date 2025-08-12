package httpServer;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubtasksTest {

    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private HttpClient client;

    HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("SubtaskTitle", "desc", epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        String jsonSubtask = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Подзадача должна успешно создаваться");

        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());
        assertTrue(getResponse.body().contains("SubtaskTitle"));
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "desc", epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addSubtask(subtask);

        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Subtask"));
    }

    @Test
    public void testGetAllSubtasksEmptyList() throws IOException, InterruptedException {
        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().equals("[]") || getResponse.body().isEmpty(),
                "Список подзадач должен быть пустым");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "desc", epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addSubtask(subtask);

        HttpResponse<String> deleteResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .DELETE()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());

        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());
        assertFalse(getResponse.body().contains("Subtask"));
    }

    @Test
    public void testGetSubtaskNotFound() throws IOException, InterruptedException {
        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Должен возвращаться 404 для несуществующей подзадачи");
    }
}