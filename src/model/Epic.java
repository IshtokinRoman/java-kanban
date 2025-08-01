package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Duration.ZERO, LocalDateTime.MAX);
        endTime = LocalDateTime.MIN;
    }

    public Epic(Epic other) {
        super(other);
        this.subtasks.addAll(other.subtasks);
        this.endTime = other.endTime;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == this.id) {
            System.out.println("id подзадачи не может быть равен id эпика!");
            return;
        }

        if (startTime.isAfter(subtask.getStartTime())) {
            startTime = subtask.getStartTime();
        } else if (endTime.isBefore(subtask.getStartTime())) {
            endTime = subtask.getEndTime();
        }

        duration = duration.plus(subtask.getDuration());
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        duration = duration.minus(subtask.getDuration());

        if (startTime.equals(subtask.getStartTime())) {
            Optional<LocalDateTime> minStartTime = subtasks.stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder());
            minStartTime.ifPresent(localDateTime -> startTime = localDateTime);
        } else if (endTime.equals(subtask.getEndTime())) {
            Optional<LocalDateTime> maxEndTime = subtasks.stream()
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());
            maxEndTime.ifPresent(localDateTime -> endTime = localDateTime);
        }
    }

    public void removeAllSubtaskIds() {
        subtasks.clear();
        duration = Duration.ZERO;
        startTime = LocalDateTime.MAX;
        endTime = LocalDateTime.MIN;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Task", "Epic") + ", subtaskIds: " + subtasks;
    }

}