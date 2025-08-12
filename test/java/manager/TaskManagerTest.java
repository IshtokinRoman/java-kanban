package manager;

import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected LocalDateTime startTime1;
    protected LocalDateTime startTime2;
    protected Duration duration;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        startTime1 = LocalDateTime.of(2024, Month.AUGUST, 30, 12, 0);
        startTime2 = LocalDateTime.of(2024, Month.AUGUST, 31, 12, 0);
        duration = Duration.ofMinutes(30);
    }

    @Test
    void shouldAddAndGetTask() {
        Task task = new Task("Title", "desc", duration, startTime1);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasksList().size(), "Ошибка: должна быть одна задача!");
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void shouldAddAndGetSubtask() {
        Epic epic = new Epic("Title", "desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Title", "desc", epic.getId(), duration, startTime1);
        taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getSubtasksList().size(), "Ошибка: должна быть одна подзадача!");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldAddAndGetEpic() {
        Epic epic = new Epic("Title", "desc");
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpicList().size(), "Ошибка: должен быть один эпик!");
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Task", "Desc", duration, startTime1);
        taskManager.addTask(task);

        task.setTitle("Updated Task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task updatedTask = new Task(task);
        taskManager.updateTask(updatedTask);

        assertEquals("Updated Task", taskManager.getTaskById(task.getId()).getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void shouldUpdateSubtaskAndEpicStatus() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId(), duration, startTime1);
        taskManager.addSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        subtask.setTitle("Updated Subtask");
        Subtask updatedSubtask = new Subtask(subtask);
        taskManager.updateSubtask(updatedSubtask);

        assertEquals("Updated Subtask", taskManager.getSubtaskById(subtask.getId()).getTitle());
        assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        epic.setTitle("Updated Epic");
        Epic updatedEpic = new Epic(epic);
        taskManager.updateEpic(updatedEpic);

        assertEquals("Updated Epic", taskManager.getEpicById(epic.getId()).getTitle());
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("Task", "Desc", duration, startTime1);
        taskManager.addTask(task);
        int id = task.getId();
        taskManager.removeTask(id);
        assertTrue(taskManager.getTasksList().isEmpty());
    }

    @Test
    void shouldRemoveSubtaskAndUpdateEpic() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId(), duration, startTime1);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask);
        taskManager.removeSubtask(subtask.getId());
        assertTrue(taskManager.getSubtasksListByEpic(epic.getId()).isEmpty());
        assertTrue(taskManager.getSubtasksListByEpic(epic.getId()).isEmpty());
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldRemoveEpicAndItsSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId(), duration, startTime1);
        taskManager.addSubtask(subtask);
        taskManager.removeEpic(epic.getId());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubtasksList().isEmpty());
        assertThrows(
                NotFoundException.class,
                () -> taskManager.getEpicById(epic.getId()),
                "Ожидалось исключение NotFoundException при добавлении пересекающейся задачи"
        );
    }

    @Test
    void shouldAddToPrioritizedTasks() {
        Task task1 = new Task("Task1", "Desc", duration, startTime1);
        Task task2 = new Task("Task2", "Desc", duration, startTime2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Iterator<Task> iterator = prioritizedTasks.iterator();
        Task firstTask = iterator.next();
        Task secondTask = iterator.next();
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task1, firstTask);
        assertEquals(task2, secondTask);
    }

    @Test
    void shouldThrowExceptionOnTaskOverlap() {
        Task task1 = new Task("Task1", "Desc", duration, startTime1);
        Task task2 = new Task("Task2", "Desc", duration, startTime1);
        taskManager.addTask(task1);
        assertThrows(
                TaskOverlapException.class,
                () -> taskManager.addTask(task2),
                "Ожидалось исключение TaskOverlapException при добавлении пересекающейся задачи"
        );

        assertEquals(1, taskManager.getTasksList().size(), "Список задач должен содержать только одну задачу");
        assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    void shouldAddToHistory() {
        Task task = new Task("Task", "Desc", duration, startTime1);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().getFirst());
    }

    @Test
    void shouldEpicStatusNew() {
        Epic epic = new Epic("Title", "desc");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Title", "desc", epic.getId(), duration, startTime1);
        Subtask subtask2 = new Subtask("Title", "desc", epic.getId(), duration, startTime2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(2, epic.getSubtasks().size());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldEpicStatusDone() {
        Epic epic = new Epic("Title", "desc");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Title", "desc", epic.getId(), duration, startTime1);
        subtask1.setStatus(TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Title", "desc", epic.getId(), duration, startTime2);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(2, epic.getSubtasks().size());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldEpicStatusInProgress() {
        Epic epic = new Epic("Title", "desc");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Title", "desc", epic.getId(), duration, startTime1);
        subtask1.setStatus(TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Title", "desc", epic.getId(), duration, startTime2);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(2, epic.getSubtasks().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
