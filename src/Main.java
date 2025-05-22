import java.util.Scanner;

public class Main {
    //если правильно понял main в этом проекте не участвует в ревью
    // и тут для себя можно было писать что угодно для тестирования работы

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
        while (true) {
            printMenu();
            Scanner scanner = new Scanner(System.in);
            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case ADD_TASK_COMMAND:
                    System.out.println("Введите заголовок задачи: ");
                    String TaskTitle = scanner.nextLine();
                    System.out.println("Введите описание");
                    String TaskDescription = scanner.nextLine();

                    Task task = new Task(TaskTitle, TaskDescription);
                    taskManager.addTask(task);
                    break;
                case ADD_EPIC_COMMAND:
                    System.out.println("Введите заголовок эпика: ");
                    String EpicTitle = scanner.nextLine();
                    System.out.println("Введите описание");
                    String EpicDescription = scanner.nextLine();

                    Epic epic = new Epic(EpicTitle, EpicDescription);
                    taskManager.addEpic(epic);

                    System.out.println("Сколько подзадач нужно?: ");
                    int subtasksNumber = scanner.nextInt();
                    scanner.nextLine();
                    for (int i = 0; i < subtasksNumber; i++) {
                        System.out.println("Введите заголовок подзадачи: ");
                        String subtaskTitle = scanner.nextLine();
                        System.out.println("Введите описание");
                        String subtaskDescription = scanner.nextLine();

                        Subtask subtask = new Subtask(subtaskTitle, subtaskDescription, epic.getId());
                        taskManager.addSubtask(subtask);
                    }
                    break;
                case VIEW_TASK_LIST_COMMAND:
                    System.out.println(taskManager.getTasksList());
                    break;
                case VIEW_EPIC_LIST_COMMAND:
                    System.out.println(taskManager.getEpicList());
                    break;
                case UPDATE_COMMAND:
                    System.out.println("Что нужно обновить: ");
                    System.out.println("1 - Задачу");
                    System.out.println("2 - Эпик");
                    System.out.println("3 - Подзадачу");
                    int updateChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (updateChoice == 1) {
                        System.out.println("Введите ID задачи:");
                        int taskId = scanner.nextInt();
                        scanner.nextLine();

                        Task taskToUpdate = taskManager.getTaskById(taskId);
                        if (taskToUpdate == null) {
                            System.out.println("Задача с таким ID не найдена.");
                            break;
                        }

                        System.out.println("Текущее состояние: " + taskToUpdate);
                        System.out.println("Введите новое название:");
                        String newTitle = scanner.nextLine();
                        System.out.println("Введите новое описание:");
                        String newDescription = scanner.nextLine();

                        System.out.println("Выберите статус (1 - NEW, 2 - IN_PROGRESS, 3 - DONE):");
                        int statusInput = scanner.nextInt();
                        scanner.nextLine();

                        TaskStatus newStatus = switch (statusInput) {
                            case 1 -> TaskStatus.NEW;
                            case 2 -> TaskStatus.IN_PROGRESS;
                            case 3 -> TaskStatus.DONE;
                            default -> null;
                        };

                        Task updatedTask = new Task(newTitle, newDescription);
                        updatedTask.setId(taskId);
                        updatedTask.setStatus(newStatus);

                        taskManager.updateTask(updatedTask);
                        System.out.println("Задача обновлена.");

                    } else if (updateChoice == 2) {
                        System.out.println("Введите ID эпика:");
                        int epicId = scanner.nextInt();
                        scanner.nextLine();

                        Epic epicToUpdate = taskManager.getEpicById(epicId);
                        if (epicToUpdate == null) {
                            System.out.println("Эпик с таким ID не найден.");
                            break;
                        }

                        System.out.println("Текущее состояние: " + epicToUpdate);
                        System.out.println("Введите новое название:");
                        String newTitle = scanner.nextLine();
                        System.out.println("Введите новое описание:");
                        String newDescription = scanner.nextLine();

                        Epic updatedEpic = new Epic(newTitle, newDescription);
                        updatedEpic.setId(epicId);
                        for (int subId : epicToUpdate.getSubtaskIds()) {
                            updatedEpic.addSubtaskId(subId);
                        }

                        taskManager.updateEpic(updatedEpic);
                        System.out.println("Эпик обновлён.");

                    } else if (updateChoice == 3) {
                        System.out.println("Введите ID подзадачи:");
                        int subtaskId = scanner.nextInt();
                        scanner.nextLine();

                        Subtask subtaskToUpdate = taskManager.getSubtaskById(subtaskId);
                        if (subtaskToUpdate == null) {
                            System.out.println("Подзадача с таким ID не найдена.");
                            break;
                        }

                        System.out.println("Текущее состояние: " + subtaskToUpdate);
                        System.out.println("Введите новое название:");
                        String newTitle = scanner.nextLine();
                        System.out.println("Введите новое описание:");
                        String newDescription = scanner.nextLine();

                        System.out.println("Выберите статус (1 - NEW, 2 - IN_PROGRESS, 3 - DONE):");
                        int statusInput = scanner.nextInt();
                        scanner.nextLine();

                        TaskStatus newStatus = switch (statusInput) {
                            case 1 -> TaskStatus.NEW;
                            case 2 -> TaskStatus.IN_PROGRESS;
                            case 3 -> TaskStatus.DONE;
                            default -> null;
                        };

                        Subtask updatedSubtask = new Subtask(newTitle, newDescription, subtaskToUpdate.getEpicId());
                        updatedSubtask.setId(subtaskId);
                        updatedSubtask.setStatus(newStatus);

                        taskManager.updateSubtask(updatedSubtask);
                        System.out.println("Подзадача обновлена.");

                    } else {
                        System.out.println("Неверный выбор.");
                    }
                    break;
                case VIEW_SUBTASK_BY_EPIC_COMMAND:
                    System.out.println("Введите id эпика:");
                    int id = scanner.nextInt();

                    System.out.println(taskManager.getSubtasksListByEpic(id));
                    break;
                case REMOVE_ALL_TASKS_COMMAND:
                    taskManager.removeAllTasks();
                    break;
                case REMOVE_ALL_EPICS_COMMAND:
                    taskManager.removeAllEpics();
                    break;
                case REMOVE_ALL_SUBTASKS_COMMAND:
                    taskManager.removeAllSubtasks();
                    break;
                case REMOVE_TASK_BY_ID_COMMAND:
                    System.out.println("Введите id удаляемой задачи");
                    int idRemoveTask = scanner.nextInt();

                    taskManager.removeTask(idRemoveTask);
                    break;
                case REMOVE_EPIC_BY_ID_COMMAND:
                    System.out.println("Введите id удаляемой эпика");
                    int idRemoveEpic = scanner.nextInt();

                    taskManager.removeEpic(idRemoveEpic);
                    break;
                case REMOVE_SUBTASK_BY_ID_COMMAND:
                    System.out.println("Введите id удаляемой подзадачи");
                    int idRemoveSubtask = scanner.nextInt();

                    taskManager.removeSubtask(idRemoveSubtask);
                    break;
                case EXIT_COMMAND:
                    System.out.println("Good Bye");
                    return;
                default:
                    return;
            }
        }
    }

    static private void printMenu() {
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