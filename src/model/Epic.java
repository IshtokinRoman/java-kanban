package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(Epic other) {
        super(other);
        this.subtaskIds.addAll(other.subtaskIds);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        if (id == this.id) {
            return;
        }
        subtaskIds.add(id);
    }

    public void removeSubtaskById(int id) {
        subtaskIds.removeIf(integer -> integer == id);
    }

    public void removeAllSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Task", "Epic") + ", subtaskIds: " + subtaskIds;
    }

}