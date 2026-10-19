package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeOfDayTest {
    @Test
    void rejectsInvalidTime() {
        assertThrows(IllegalArgumentException.class, () -> new TimeOfDay(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new TimeOfDay(24, 0));
        assertThrows(IllegalArgumentException.class, () -> new TimeOfDay(12, -1));
        assertThrows(IllegalArgumentException.class, () -> new TimeOfDay(12, 60));
    }

    @Test
    void comparesHoursThenMinutes() {
        assertTrue(new TimeOfDay(12, 59).compareTo(new TimeOfDay(13, 0)) < 0);
        assertTrue(new TimeOfDay(13, 5).compareTo(new TimeOfDay(13, 45)) < 0);
        assertEquals(0, new TimeOfDay(13, 0).compareTo(new TimeOfDay(13, 0)));
    }

    @Test
    void equalTimesHaveEqualHashCodes() {
        TimeOfDay first = new TimeOfDay(13, 0);
        TimeOfDay second = new TimeOfDay(13, 0);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals("13:00", first.toString());
    }
}
