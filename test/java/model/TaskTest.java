package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private LocalDateTime startTime;
    private Duration duration;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2025, Month.AUGUST, 1, 1, 1);
        duration = Duration.ofMinutes(30);
        task = new Task("Test", "desc", duration, startTime);
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task secondTask = new Task("Test", "desc", duration, startTime.plusDays(1));

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