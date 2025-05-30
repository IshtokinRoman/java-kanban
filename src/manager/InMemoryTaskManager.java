package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int getNewId() {
        return idCounter++;
    }

    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(getNewId());
        } else {
            if (task.getId() >= idCounter) {
                idCounter = task.getId() + 1;
            }
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(getNewId());
        } else {
            if (epic.getId() >= idCounter) {
                idCounter = epic.getId() + 1;
            }
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Subtask> getSubtasksListByEpic(int epicId) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksInEpic.add(subtask);
            }
        }
        return subtasksInEpic;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(getNewId());
        } else {
            if (subtask.getId() >= idCounter) {
                idCounter = subtask.getId() + 1;
            }
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        subtasks.put(subtaskToUpdate.getId(), subtaskToUpdate);

        int epicId = subtaskToUpdate.getEpicId();
        int subtasksDone = 0;
        int subtasksNumber = getSubtasksListByEpic(epicId).size();
        Epic epic = getEpicById(epicId);

        for (Subtask subtask : getSubtasksListByEpic(epicId)) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                subtasksDone++;
            }
        }
        if (subtasksDone == subtasksNumber) {
            epic.setStatus(TaskStatus.DONE);
            return;
        } else if (subtasksDone != 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            return;
        }
        epic.setStatus(TaskStatus.NEW);
    }

    @Override
    public void removeSubtask(int id) {
        Epic epic = getEpicById(getSubtaskById(id).getEpicId());
        epic.removeSubtaskById(id);
        subtasks.remove(id);

        int subtasksDone = 0;
        int subtasksNumber = getSubtasksListByEpic(epic.getId()).size();

        for (Subtask subtask : getSubtasksListByEpic(epic.getId())) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                subtasksDone++;
            }
        }
        if (subtasksDone == subtasksNumber) {
            epic.setStatus(TaskStatus.DONE);
            return;
        } else if (subtasksDone != 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            return;
        }
        epic.setStatus(TaskStatus.NEW);
    }

    @Override
    public void removeAllSubtasks() {
        for(Integer id : epics.keySet()) {
            Epic epic = getEpicById(id);
            epic.removeAllSubtaskIds();
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}