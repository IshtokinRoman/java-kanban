package manager;

import Exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int getNewId() {
        return super.getNewId();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return super.getTasksList();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksListByEpic(int epicId) {
        return super.getSubtasksListByEpic(epicId);
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return super.getEpicList();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return super.getSubtasksList();
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        super.updateSubtask(subtaskToUpdate);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public void save() {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            bf.write("id,type,title,status,description,epic\n");

            for (Task task : tasks.values()) {
                bf.write(toCsv(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                bf.write(toCsv(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                bf.write(toCsv(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: " + file.getName(), e);
        }
    }

    private String toCsv(Task task) {
        String epicId;

        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            epicId = "";
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(), task.getType(), task.getTitle(), task.getStatus(), task.getDescription(), epicId);
    }

    private static void fromCsv(FileBackedTaskManager manager, String line) {
        String[] items = line.split(",");
        int id;

        try {
            id = Integer.parseInt(items[0]);
        } catch (Exception ex) {
            System.out.println("Ошибка считывания поля id!");
            id = -1;
        }

        TaskType type = TaskType.valueOf(items[1]);
        String tittle = items[2];
        TaskStatus status = TaskStatus.valueOf(items[3]);
        String description = items[4];
        int epicId = -1;

        if (items.length > 5 && !items[5].isEmpty()) {
            try {
                epicId = Integer.parseInt(items[5]);
            } catch (Exception ex) {
                System.out.println("Ошибка считывания поля epicId!");
            }
        }

        switch (type) {
            case TASK:
                Task task = new Task(tittle, description);
                task.setId(id);
                task.setStatus(status);
                manager.addTask(task);
                break;
            case EPIC:
                Epic epic = new Epic(tittle, description);
                epic.setId(id);
                epic.setStatus(status);
                manager.addEpic(epic);
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(tittle, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                manager.addSubtask(subtask);
                break;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            br.readLine();

            while (br.ready()) {
                fromCsv(fileBackedTaskManager, br.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }
        return fileBackedTaskManager;
    }
}