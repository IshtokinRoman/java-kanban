package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void historyShouldStoreOriginalVersionOfTask() {
        Task originalTask = new Task("Task", "desc");
        taskManager.addTask(originalTask);

        Task retrieved = taskManager.getTaskById(originalTask.getId());
        assertNotNull(retrieved);
        assertEquals(TaskStatus.NEW, retrieved.getStatus());

        originalTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(originalTask);
        List<Task> history = taskManager.getHistory();

        Task taskFromHistory = history.getFirst();

        assertNotNull(taskFromHistory);

        assertEquals(TaskStatus.NEW, taskFromHistory.getStatus());
    }

    @Test
    void historyShouldStoreLatestVersionOfTask() {
        Task task = new Task("Task", "desc");
        taskManager.addTask(task);

        Epic epic = new Epic("Epic", "desc");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Title", "desc", epic.getId());
        taskManager.addSubtask(subtask);
        epic.addSubtaskId(subtask.getId());

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<Task> tasks = taskManager.getHistory();

        assertEquals(epic.getId(), tasks.getFirst().getId());
        assertEquals(task.getId(), tasks.get(1).getId());
        assertEquals(subtask.getId(), tasks.get(2).getId());
    }
}