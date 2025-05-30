package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        int maxHistorySize = 10;

        if (historyList.size() >= maxHistorySize) {
            historyList.removeLast();
        }

        Task copy;
        if (task instanceof Epic epic) {
            Epic epicCopy = new Epic(epic.getTitle(), epic.getDescription());

            epicCopy.setId(epic.getId());
            epicCopy.setStatus(epic.getStatus());
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epicCopy.addSubtaskId(subtaskId);
            }
            copy = epicCopy;
        } else if (task instanceof Subtask subtask) {
            model.Subtask subtaskCopy = new model.Subtask(subtask.getTitle(), subtask.getDescription(),
                    subtask.getEpicId());

            subtaskCopy.setId(subtask.getId());
            subtaskCopy.setStatus(subtask.getStatus());
            copy = subtaskCopy;
        } else {
            Task taskCopy = new Task(task.getTitle(), task.getDescription());

            taskCopy.setId(task.getId());
            taskCopy.setStatus(task.getStatus());
            copy = taskCopy;
        }

        historyList.add(copy);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
