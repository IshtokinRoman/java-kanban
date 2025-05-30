package manager;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void historyShouldStoreOriginalVersionOfTask() {
        TaskManager taskManager = Managers.getDefault();

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
}