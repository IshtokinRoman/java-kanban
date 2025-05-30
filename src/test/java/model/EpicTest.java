package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic Title", "Epic Description");
    }

    @Test
    void shouldAddSubtaskId() {
        epic.addSubtaskId(1);

        List<Integer> list = new ArrayList<>();

        list.add(1);
        assertEquals(list.getFirst(), epic.getSubtaskIds().getFirst());
    }

    @Test
    void shouldCreateEpicWithEmptySubtaskList() {
        assertEquals("Epic Title", epic.getTitle());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void shouldRemoveSubtaskById() {
        List<Integer> list = new ArrayList<>();

        epic.addSubtaskId(1);
        epic.addSubtaskId(2);
        epic.removeSubtaskById(2);

        list.add(1);
        assertEquals(list.getFirst(), epic.getSubtaskIds().getFirst());
    }

    @Test
    void shouldAddSubtaskWithSubtaskListWithEpicId() {
        epic.addSubtaskId(epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty());
    }
}