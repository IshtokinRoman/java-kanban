package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private LocalDateTime startTime1;
    private LocalDateTime startTime2;
    private Duration duration;

    @BeforeEach
    void setUp() {
        startTime1 = LocalDateTime.of(2024, Month.AUGUST, 30,12, 0);
        startTime2 = LocalDateTime.of(2024, Month.AUGUST, 31,12, 0);
        duration = Duration.ofMinutes(30);
        taskManager = Managers.getDefault();
    }

    @Test
    void historyShouldStoreOriginalVersionOfTask() {
        Task originalTask = new Task("Task", "desc", duration, startTime1);
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
        Task task = new Task("Task", "desc", duration, startTime1);
        taskManager.addTask(task);

        Epic epic = new Epic("Epic", "desc");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Title", "desc", epic.getId(), duration, startTime2);
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<Task> tasks = taskManager.getHistory();

        assertEquals(3, tasks.size());
        assertEquals(epic.getId(), tasks.getFirst().getId());
        assertEquals(task.getId(), tasks.get(1).getId());
        assertEquals(subtask.getId(), tasks.get(2).getId());
    }

    @Test
    void shouldRemoveTaskFromStartOfHistory() {
        Task task1 = new Task("Task1", "Desc", duration, startTime1);
        Task task2 = new Task("Task2", "Desc", duration, startTime2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.removeFromHistory(task1.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Оставшаяся задача должна быть task2");
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task("Task1", "Desc", duration, startTime1);
        Task task2 = new Task("Task2", "Desc", duration, startTime2);
        Task task3 = new Task("Task3", "Desc", duration, startTime2.plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.removeFromHistory(task2.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        Task task1 = new Task("Task1", "Desc", duration, startTime1);
        Task task2 = new Task("Task2", "Desc", duration, startTime2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.removeFromHistory(task2.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task1, history.getFirst(), "Оставшаяся задача должна быть task1");
    }
}