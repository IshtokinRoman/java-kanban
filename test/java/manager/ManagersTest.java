package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
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

        Subtask subtask1 = new Subtask("Subtask1", "desc", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "desc", epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Subtask1 должен быть удалён");
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Subtask2 должен быть удалён");
    }

    @Test
    void shouldNotAffectTaskManagerIfTaskChangedOutside() {
        Task task = new Task("Task", "desc");
        taskManager.addTask(task);

        Task fromManager = taskManager.getTaskById(task.getId());
        fromManager.setTitle("Changed Title");

        Task stored = taskManager.getTaskById(task.getId());
        assertEquals("Task", stored.getTitle(), "Менеджер не должен 'заметить' изменения напрямую");
    }
}