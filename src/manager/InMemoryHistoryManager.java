package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY_SIZE = 10;
    private static final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() >= MAX_HISTORY_SIZE) {
            historyList.removeLast();
        }

        Task savedTask = CloneManager.cloneTask(task);
        historyList.add(savedTask);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
