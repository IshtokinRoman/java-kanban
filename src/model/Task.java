package model;

import java.util.Objects;

public class Task {
    protected int id;
    protected TaskStatus status;
    protected String title;
    protected String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
    }

    public Task(Task other) {
        this.id = other.id;
        this.title = other.title;
        this.description = other.description;
        this.status = other.status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus taskStatus) {
        status = taskStatus;
    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task id: " + id + ", title: '" + title + "', description: '" +
                description + "', status: '" + status + "'";
    }

    public void setTitle(String title) {
        this.title = title;
    }
}