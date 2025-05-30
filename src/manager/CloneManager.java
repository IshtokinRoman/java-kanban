package manager;

import model.Epic;
import model.Subtask;
import model.Task;

public class CloneManager {

    public static Task cloneTask(Task task) {
        if (task instanceof Subtask subtask) {
            Subtask copy = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getEpicId());

            copy.setId(subtask.getId());
            copy.setStatus(subtask.getStatus());
            return copy;
        } else if (task instanceof Epic epic) {
            Epic copy = new Epic(epic.getTitle(), epic.getDescription());

            copy.setId(epic.getId());
            copy.setStatus(epic.getStatus());
            for (Integer subtaskId : epic.getSubtaskIds()) {
                copy.addSubtaskId(subtaskId);
            }
            return copy;
        } else {
            Task copy = new Task(task.getTitle(), task.getDescription());

            copy.setId(task.getId());
            copy.setStatus(task.getStatus());
            return copy;
        }
    }
}
