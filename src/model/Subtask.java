package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
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
}