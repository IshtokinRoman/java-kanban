package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic Title", "Epic Description");
        epic.setId(1);
        LocalDateTime startTime1 = LocalDateTime.of(2024, Month.AUGUST, 30,12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, Month.AUGUST, 31,12, 0);
        Duration duration = Duration.ofMinutes(30);
        subtask1 = new Subtask("title1", "desc1", epic.getId(), duration, startTime1);
        subtask1.setId(2);
        subtask2 = new Subtask("title2", "desc2", epic.getId(), duration, startTime2);
        subtask2.setId(3);
    }

    @Test
    void shouldAddSubtaskId() {
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        int listSize = epic.getSubtasks().size();

        assertEquals(2, listSize);
    }

    @Test
    void shouldCreateEpicWithEmptySubtaskList() {
        assertEquals("Epic Title", epic.getTitle());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveSubtaskById() {

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.removeSubtask(subtask2);

        int listSize = epic.getSubtasks().size();

        assertEquals(1, listSize);
    }
}