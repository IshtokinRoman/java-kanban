package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldNotAllowSubtaskToReferenceItselfAsEpic() {
        Subtask subtask = new Subtask("Subtask title", "Subtask desc", 1);
        subtask.setId(1);

        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }

}