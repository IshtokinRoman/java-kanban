package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

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
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicIsDeleted() {
        Epic epic = new Epic("Epic", "description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "desc", epic.getId(), duration, startTime1);
        Subtask subtask2 = new Subtask("Subtask2", "desc", epic.getId(), duration, startTime2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Subtask1 должен быть удалён");
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Subtask2 должен быть удалён");
    }

    @Test
    void shouldNotAffectTaskManagerIfTaskChangedOutside() {
        Task task = new Task("Task", "desc", duration, startTime2);
        taskManager.addTask(task);

        Task fromManager = taskManager.getTaskById(task.getId());
        fromManager.setTitle("Changed Title");

        Task stored = taskManager.getTaskById(task.getId());
        assertEquals("Task", stored.getTitle(), "Менеджер не должен 'заметить' изменения напрямую");
    }
}