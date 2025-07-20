package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    @TempDir
    File tempDir; // Временная папка для тестов
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".txt", tempDir);
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndRemoveTasks() throws IOException {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        assertEquals(2, manager.tasks.size(), "Должно быть две задачи");

        BufferedReader br = new BufferedReader(new FileReader(tempFile, StandardCharsets.UTF_8));
        String firstLine = br.readLine();
        String secondLine = br.readLine();
        String firstLineExpected = "id,type,title,status,description,epic";
        String secondLineExpected = "1,TASK,Задача 1,NEW,Описание задачи 1,";

        assertEquals(firstLineExpected, firstLine, "Несовпадение в первой строке!");
        assertEquals(secondLineExpected, secondLine, "Несовпадение в второй строке!");

        manager.removeAllTasks();

        assertEquals(0, manager.tasks.size(), "Все таски должны быть удалены!");

        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.tasks.size(), "Должна быть одна задача!");
        assertEquals(1, loadedManager.epics.size(), "Должен быть один эпик!");
        assertEquals(1, loadedManager.subtasks.size(), "Должна быть одна подзадача!");
    }
}
