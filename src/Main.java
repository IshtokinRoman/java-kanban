import manager.TaskManager;
import model.TaskStatus;
import model.*;
import java.util.Scanner;

public class Main {
    private static final int ADD_TASK_COMMAND = 1;
    private static final int ADD_EPIC_COMMAND = 2;
    private static final int VIEW_TASK_LIST_COMMAND = 3;
    private static final int VIEW_EPIC_LIST_COMMAND = 4;
    private static final int UPDATE_COMMAND = 5;
    private static final int VIEW_SUBTASK_BY_EPIC_COMMAND = 6;
    private static final int REMOVE_ALL_TASKS_COMMAND = 7;
    private static final int REMOVE_ALL_EPICS_COMMAND = 8;
    private static final int REMOVE_ALL_SUBTASKS_COMMAND = 9;
    private static final int REMOVE_TASK_BY_ID_COMMAND = 10;
    private static final int REMOVE_EPIC_BY_ID_COMMAND = 11;
    private static final int REMOVE_SUBTASK_BY_ID_COMMAND = 12;
    private static final int EXIT_COMMAND = 13;

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case ADD_TASK_COMMAND -> addTask(taskManager, scanner);
                case ADD_EPIC_COMMAND -> addEpicWithSubtasks(taskManager, scanner);
                case VIEW_TASK_LIST_COMMAND -> System.out.println(taskManager.getTasksList());
                case VIEW_EPIC_LIST_COMMAND -> System.out.println(taskManager.getEpicList());
                case UPDATE_COMMAND -> updateItem(taskManager, scanner);
                case VIEW_SUBTASK_BY_EPIC_COMMAND -> viewSubtasksByEpic(taskManager, scanner);
                case REMOVE_ALL_TASKS_COMMAND -> taskManager.removeAllTasks();
                case REMOVE_ALL_EPICS_COMMAND -> taskManager.removeAllEpics();
                case REMOVE_ALL_SUBTASKS_COMMAND -> taskManager.removeAllSubtasks();
                case REMOVE_TASK_BY_ID_COMMAND -> removeTaskById(taskManager, scanner);
                case REMOVE_EPIC_BY_ID_COMMAND -> removeEpicById(taskManager, scanner);
                case REMOVE_SUBTASK_BY_ID_COMMAND -> removeSubtaskById(taskManager, scanner);
                case EXIT_COMMAND -> {
                    System.out.println("Good Bye");
                    return;
                }
                default -> { return; }
            }
        }
    }

    private static void addTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите заголовок задачи: ");
        String title = scanner.nextLine();
        System.out.println("Введите описание");
        String description = scanner.nextLine();

        taskManager.addTask(new Task(title, description));
    }

    private static void addEpicWithSubtasks(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите заголовок эпика: ");
        String title = scanner.nextLine();
        System.out.println("Введите описание");
        String description = scanner.nextLine();

        Epic epic = new Epic(title, description);
        taskManager.addEpic(epic);

        System.out.println("Сколько подзадач нужно?: ");
        int count = scanner.nextInt();
        scanner.nextLine();


        for (int i = 0; i < count; i++) {
            System.out.println("Введите заголовок подзадачи: ");
            String subTitle = scanner.nextLine();
            System.out.println("Введите описание");
            String subDescription = scanner.nextLine();
            taskManager.addSubtask(new Subtask(subTitle, subDescription, epic.getId()));
        }
    }

    private static void updateItem(TaskManager taskManager, Scanner scanner) {
        System.out.println("Что нужно обновить: ");
        System.out.println("1 - Задачу");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадачу");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1 -> updateTask(taskManager, scanner);
            case 2 -> updateEpic(taskManager, scanner);
            case 3 -> updateSubtask(taskManager, scanner);
            default -> System.out.println("Неверный выбор.");
        }
    }

    private static void updateTask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID задачи:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Task task = taskManager.getTaskById(id);
        System.out.println(task);
        System.out.println("Введите новое название:");
        String title = scanner.nextLine();
        System.out.println("Введите новое описание:");
        String description = scanner.nextLine();
        System.out.println("Выберите статус (1 - NEW, 2 - IN_PROGRESS, 3 - DONE):");
        TaskStatus status = readStatus(scanner);

        Task updated = new Task(title, description);
        updated.setId(id);
        updated.setStatus(status);
        taskManager.updateTask(updated);
        System.out.println("Задача обновлена.");
    }

    private static void updateEpic(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID эпика:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Epic epic = taskManager.getEpicById(id);
        System.out.println("Текущее состояние: " + epic);
        System.out.println("Введите новое название:");
        String title = scanner.nextLine();
        System.out.println("Введите новое описание:");
        String description = scanner.nextLine();

        Epic updated = new Epic(title, description);
        updated.setId(id);
        for (int subId : epic.getSubtaskIds()) {
            updated.addSubtaskId(subId);
        }
        taskManager.updateEpic(updated);
        System.out.println("Эпик обновлён.");
    }

    private static void updateSubtask(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите ID подзадачи:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Subtask subtask = taskManager.getSubtaskById(id);
        System.out.println("Текущее состояние: " + subtask);
        System.out.println("Введите новое название:");
        String title = scanner.nextLine();
        System.out.println("Введите новое описание:");
        String description = scanner.nextLine();
        System.out.println("Выберите статус (1 - NEW, 2 - IN_PROGRESS, 3 - DONE):");
        TaskStatus status = readStatus(scanner);

        Subtask updated = new Subtask(title, description, subtask.getEpicId());
        updated.setId(id);
        updated.setStatus(status);
        taskManager.updateSubtask(updated);
        System.out.println("Подзадача обновлена.");
    }

    private static TaskStatus readStatus(Scanner scanner) {
        int input = scanner.nextInt();
        scanner.nextLine();
        return switch (input) {
            case 1 -> TaskStatus.NEW;
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.DONE;
            default -> null;
        };
    }

    private static void viewSubtasksByEpic(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите id эпика:");
        int id = scanner.nextInt();
        System.out.println(taskManager.getSubtasksListByEpic(id));
    }

    private static void removeTaskById(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите id удаляемой задачи");
        int id = scanner.nextInt();
        taskManager.removeTask(id);
    }

    private static void removeEpicById(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите id удаляемой эпика");
        int id = scanner.nextInt();
        taskManager.removeEpic(id);
    }

    private static void removeSubtaskById(TaskManager taskManager, Scanner scanner) {
        System.out.println("Введите id удаляемой подзадачи");
        int id = scanner.nextInt();
        taskManager.removeSubtask(id);
    }

    private static void printMenu() {
        System.out.println("Вас приветствует трекер задач, введите нужную команду: ");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Создать эпик");
        System.out.println("3 - Вывести список всех задач");
        System.out.println("4 - Вывести список эпиков");
        System.out.println("5 - Обновить задачу/эпик");
        System.out.println("6 - Вывести список подзадач эпика по id");
        System.out.println("7 - Удалить все задачи");
        System.out.println("8 - Удалить все эпики");
        System.out.println("9 - Удалить все подзадачи");
        System.out.println("10 - Удалить задачу по id");
        System.out.println("11 - Удалить эпик по id");
        System.out.println("12 - Удалить подзадачу по id");
        System.out.println("13 - Выйти из приложения");
    }
}