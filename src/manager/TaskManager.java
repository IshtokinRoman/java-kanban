package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void addEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getSubtasksListByEpic(int epicId) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksInEpic.add(subtask);
            }
        }
        return subtasksInEpic;
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }


    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

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

    public void removeSubtask(int id) {
        Epic epic = getEpicById(getSubtaskById(id).getEpicId());
        epic.removeSubtaskById(id);
        subtasks.remove(id);

        //для проверки статуса без удаленного сабтаска
        //получается дублирование кода с апдейта сабтаска, не знаю как лучше сделать
        //если правильно понимаю то лучше вывести в отдельный метод
        //но по тз нельзя создавать метод отдельно для обновления статуса
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

    public void removeAllSubtasks() {
        for(Integer id : epics.keySet()) {
            Epic epic = getEpicById(id);
            epic.removeAllSubtaskIds();
        }
        subtasks.clear();
    }

    public static int getNewId() {
        return idCounter++;
    }
}