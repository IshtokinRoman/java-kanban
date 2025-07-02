package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyList = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        Task copy;

        if (task instanceof Epic epic) {
            copy = new Epic(epic);
        } else if (task instanceof Subtask subtask) {
            copy = new Subtask(subtask);
        } else {
            copy = new Task(task);
        }

        if (historyList.containsKey(copy.getId())) {
            remove(copy.getId());
        }

        Node newNode = linkLast(copy);
        historyList.put(copy.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (historyList.containsKey(id)) {
            Node removingNode = historyList.get(id);

            removeNode(removingNode);
            historyList.remove(id);
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task, tail, null);

        if (tail != null) {
            tail.setNext(newNode);
        } else {
            head = newNode;
        }

        tail = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        } else {
            head = next;
        }

        if (next != null) {
            next.setPrev(prev);
        } else {
            tail = prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;

        while (current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }

        return tasks;
    }
}