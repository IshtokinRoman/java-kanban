package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Test", "desc");
    }

    //запутался, по тз вроде в предыдущем спринте нужно было чтобы equals возвращал false при разных id
    //потом в ревью было сказано что одного id мало и нужно сделать по всем полям поэтому тест по всем полям
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task secondTask = new Task("Test", "desc");

        assertEquals(task, secondTask);
    }

    @Test
    void shouldCreateTaskWithDefaultStatusNEW() {
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldSetAndGetId() {
        task.setId(10);
        assertEquals(10, task.getId());
    }

    @Test
    void shouldUpdateStatus() {
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }
}