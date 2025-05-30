package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int getNewId();

    void addTask(Task task);

    Task getTaskById(int id);

    ArrayList<Task> getTasksList();

    void updateTask(Task task);

    void removeTask(int id);

    void removeAllTasks();

    void addEpic(Epic epic);

    Epic getEpicById(int id);

    ArrayList<Subtask> getSubtasksListByEpic(int epicId);

    ArrayList<Epic> getEpicList();

    void updateEpic(Epic epic);

    void removeEpic(int id);

    void removeAllEpics();

    void addSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    ArrayList<Subtask> getSubtasksList();

    void updateSubtask(Subtask subtaskToUpdate);

    void removeSubtask(int id);

    void removeAllSubtasks();

    List<Task> getHistory();
}