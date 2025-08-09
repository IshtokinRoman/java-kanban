package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldNotAllowSubtaskToReferenceItselfAsEpic() {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.AUGUST, 1, 1, 1);
        Duration duration = Duration.ofMinutes(30);
        Subtask subtask = new Subtask("Subtask title", "Subtask desc", 1, duration, startTime);
        subtask.setId(1);

        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }

}