package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания файла!", e);
        }
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenLoadingNonExistentFile() {
        File nonExistentFile = new File(tempFile, "non_existent.csv");

        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        }, "Должно выбросить ManagerSaveException при попытке загрузки несуществующего файла");

        assertTrue(exception.getMessage().contains("Ошибка при загрузке файла"));
        assertInstanceOf(IOException.class, exception.getCause(), "Причина должна быть IOException");
    }

    @Test
    void shouldSaveAndLoadTasksSuccessfully() {
        Task task = new Task("Task", "Desc", duration, startTime1);
        Epic epic = new Epic("Epic", "Desc");

        assertDoesNotThrow(() -> {
            taskManager.addTask(task);
            taskManager.addEpic(epic);
            Subtask subtask = new Subtask("Subtask", "Desc", epic.getId(), duration, startTime2);
            taskManager.addSubtask(subtask);
        }, "Сохранение задач должно выполняться без исключений");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getTasksList().size(), "Должна быть одна задача");
        assertEquals(1, loadedManager.getEpicList().size(), "Должен быть один эпик");
        assertEquals(1, loadedManager.getSubtasksList().size(), "Должна быть одна подзадача");

        Task loadedTask = loadedManager.getTaskById(task.getId());
        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        Subtask loadedSubtask = loadedManager.getSubtasksList().getFirst();

        assertNotNull(loadedTask, "Загруженная задача не должна быть null");
        assertEquals(task.getTitle(), loadedTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Статус задачи должен совпадать");

        assertNotNull(loadedEpic, "Загруженный эпик не должен быть null");
        assertEquals(epic.getTitle(), loadedEpic.getTitle(), "Название эпика должно совпадать");

        assertNotNull(loadedSubtask, "Загруженная подзадача не должна быть null");
        assertEquals("Subtask", loadedSubtask.getTitle(), "Название подзадачи должно совпадать");
        assertEquals(epic.getId(), loadedSubtask.getEpicId(), "ID эпика подзадачи должен совпадать");
    }
}