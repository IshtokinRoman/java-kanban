package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask other) {
        super(other);
        this.epicId = other.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Task", "model.Subtask");
    }

    @Override
    public void setId(int id) {
        while (id == epicId) {
            id++;
        }
        this.id = id;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}