package httpServer;

import com.google.gson.Gson;
import handler.EpicHandler;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.TaskStatus;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerEpicsTest {

    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private HttpClient client;

    HttpTaskManagerEpicsTest() throws IOException {
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
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("EpicTest", "desc");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setStartTime(LocalDateTime.now());
        String jsonEpic = gson.toJson(epic);

        HttpResponse<String> postResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, postResponse.statusCode());

        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("EpicTest"));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "desc");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(5));
        epic.setStartTime(LocalDateTime.now());
        String jsonEpic = gson.toJson(epic);

        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build(), HttpResponse.BodyHandlers.ofString());

        // Удаляем
        HttpResponse<String> deleteResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());

        // Проверяем что пусто
        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());
        assertFalse(getResponse.body().contains("Title"));
    }

    @Test
    public void testGetEpicNotFound() throws IOException, InterruptedException {
        HttpResponse<String> getResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/2"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Должен возвращаться 404 для несуществующего эпика");
    }
}
