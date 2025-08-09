package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public int getNewId() {
        return idCounter++;
    }

    public void addTask(Task task) {
        if (hasNotOverlapping(task)) {
            if (task.getId() == 0) {
                task.setId(getNewId());
            } else {
                if (task.getId() >= idCounter) {
                    idCounter = task.getId() + 1;
                }
            }
            tasks.put(task.getId(), task);

            if (task.getStartTime() != LocalDateTime.MAX && task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return new Task(task);
        }

        return null;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task taskToUpdate) {
        Task existingTask = tasks.get(taskToUpdate.getId());

        if (existingTask != null) {
            prioritizedTasks.remove(existingTask);
        }

        if (taskToUpdate.getStartTime() == LocalDateTime.MAX) {
            prioritizedTasks.add(taskToUpdate);
        }
        tasks.put(taskToUpdate.getId(), taskToUpdate);
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        prioritizedTasks.removeAll(tasks.values());
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
            return new Epic(epic);
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtasksListByEpic(int epicId) {
        Epic epic = epics.get(epicId);

        return epic.getSubtasks();
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
            epic.getSubtasks()
                    .forEach(subtask -> subtasks.remove(subtask.getId()));
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
        if (hasNotOverlapping(subtask)) {
        if (subtask.getId() == 0) {
            subtask.setId(getNewId());
        } else {
            if (subtask.getId() >= idCounter) {
                idCounter = subtask.getId() + 1;
            }
        }
        subtasks.put(subtask.getId(), subtask);

        LocalDateTime startTimeSubtask = subtasks.get(subtask.getId()).getStartTime();

        if (startTimeSubtask != LocalDateTime.MAX && startTimeSubtask != null) {
            prioritizedTasks.add(subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        setStatusForEpic(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
            return new Subtask(subtask);
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        Subtask existingSubtask = subtasks.get(subtaskToUpdate.getId());
        LocalDateTime startTime = existingSubtask.getStartTime();
        prioritizedTasks.remove(existingSubtask);

        if (startTime != LocalDateTime.MAX && startTime != null) {
            prioritizedTasks.add(subtaskToUpdate);
        }

        //subtasks.put(subtaskToUpdate.getId(), subtaskToUpdate);
        addSubtask(subtaskToUpdate);

        int epicId = subtaskToUpdate.getEpicId();
        Epic epic = getEpicById(epicId);

        epic.removeSubtask(existingSubtask);
        epic.addSubtask(subtaskToUpdate);

        setStatusForEpic(epic);
    }

    @Override
    public void removeSubtask(int id) {
        Epic epic = epics.get(getSubtaskById(id).getEpicId());
        Subtask subtaskToRemove = subtasks.get(id);
        epic.removeSubtask(subtaskToRemove);
        subtasks.remove(id);
        prioritizedTasks.remove(subtaskToRemove);

        setStatusForEpic(epic);
    }

    private void setStatusForEpic(Epic epic) {
        int subtasksNumber = getSubtasksListByEpic(epic.getId()).size();

        int inProgressSubtasks = Math.toIntExact(epic.getSubtasks().stream()
                .filter(subtask -> subtask.getStatus() == TaskStatus.IN_PROGRESS)
                .count());
        int doneSubtasks = Math.toIntExact(epic.getSubtasks().stream()
                .filter(subtask -> subtask.getStatus() == TaskStatus.DONE)
                .count());

        if (doneSubtasks == subtasksNumber && doneSubtasks != 0) {
            epic.setStatus(TaskStatus.DONE);
        } else if (doneSubtasks > 0 || inProgressSubtasks > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : epics.keySet()) {
            Epic epic = getEpicById(id);
            epic.removeAllSubtaskIds();
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    public void removeFromHistory(int id) {
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getDuration() == null ||
                task2.getStartTime() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    public boolean hasNotOverlapping(Task task) {
        return prioritizedTasks.stream()
                .filter(task1 -> task1.getId() != task.getId())
                .noneMatch(task1 -> isOverlapping(task1, task));
    }
}