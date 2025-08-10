package httpServer;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerPrioritizedTest {

    private TaskManager taskManager;
    private HttpTaskServer server;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testGetPrioritizedListWithTasks() throws IOException, InterruptedException {
        Task t1 = new Task("Task1", "desc",
                Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(30));
        Task t2 = new Task("Task2", "desc",
                Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(100));
        Task t3 = new Task("Task3", "desc",
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(500));

        taskManager.addTask(t1);
        taskManager.addTask(t2);
        taskManager.addTask(t3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен вернуться статус 200");

        String body = response.body();

        int idxT1 = body.indexOf("Task1");
        int idxT2 = body.indexOf("Task2");
        int idxT3 = body.indexOf("Task3");

        assertTrue(idxT2 > idxT1 && idxT1 < idxT3, "Задачи должны быть в порядке времени начала");
    }
}
