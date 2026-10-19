package ru.yandex.practicum.gym;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimetableTest {
    private final Timetable timetable = new Timetable();
    private final Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
    private final Group children = new Group("Акробатика для детей", Age.CHILD, 60);

    private TrainingSession add(Coach trainer, DayOfWeek day, int hours, int minutes) {
        TrainingSession session = new TrainingSession(children, trainer, day,
                new TimeOfDay(hours, minutes));
        timetable.addNewTrainingSession(session);
        return session;
    }

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        TrainingSession session = add(coach, DayOfWeek.MONDAY, 13, 0);
        assertEquals(List.of(session), timetable.getTrainingSessionsForDay(
                DayOfWeek.MONDAY).get(new TimeOfDay(13, 0)));
        assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Group adults = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession late = new TrainingSession(adults, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));
        timetable.addNewTrainingSession(late);
        TrainingSession monday = add(coach, DayOfWeek.MONDAY, 13, 0);
        TrainingSession early = add(coach, DayOfWeek.THURSDAY, 13, 0);
        add(coach, DayOfWeek.SATURDAY, 10, 0);
        assertEquals(List.of(monday), timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        assertEquals(List.of(List.of(early), List.of(late)),
                new ArrayList<>(timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY).values()));
        assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        TrainingSession session = add(coach, DayOfWeek.MONDAY, 13, 0);
        assertEquals(List.of(session), timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        assertTrue(timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0)).isEmpty());
    }

    @Test
    void emptyTimetableHasNoSessionsOnAnyDay() {
        for (DayOfWeek day : DayOfWeek.values()) {
            assertTrue(timetable.getTrainingSessionsForDay(day).isEmpty());
            assertTrue(timetable.getTrainingSessionsForDayAndTime(day, new TimeOfDay(0, 0)).isEmpty());
        }
    }

    @Test
    void sameTimeKeepsAllSessions() {
        TrainingSession first = add(coach, DayOfWeek.MONDAY, 13, 0);
        TrainingSession second = add(new Coach("Иванов", "Иван", "Иванович"),
                DayOfWeek.MONDAY, 13, 0);
        assertEquals(List.of(first, second), timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        assertEquals(2, timetable.getTrainingSessionsForDay(
                DayOfWeek.MONDAY).get(new TimeOfDay(13, 0)).size());
    }

    @Test
    void sortsMinutesAndBoundaryTimes() {
        add(coach, DayOfWeek.SUNDAY, 23, 59);
        add(coach, DayOfWeek.SUNDAY, 13, 45);
        add(coach, DayOfWeek.SUNDAY, 0, 0);
        add(coach, DayOfWeek.SUNDAY, 13, 5);
        assertEquals(List.of(new TimeOfDay(0, 0), new TimeOfDay(13, 5),
                new TimeOfDay(13, 45), new TimeOfDay(23, 59)),
                new ArrayList<>(timetable.getTrainingSessionsForDay(DayOfWeek.SUNDAY).keySet()));
    }

    @Test
    void sameTimeOnDifferentDaysIsIndependent() {
        TrainingSession monday = add(coach, DayOfWeek.MONDAY, 13, 0);
        TrainingSession sunday = add(coach, DayOfWeek.SUNDAY, 13, 0);
        assertEquals(List.of(monday), timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        assertEquals(List.of(sunday), timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.SUNDAY, new TimeOfDay(13, 0)));
    }

    @Test
    void queryResultsCannotModifySchedule() {
        add(coach, DayOfWeek.MONDAY, 13, 0);
        NavigableMap<TimeOfDay, List<TrainingSession>> day
                = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assertThrows(UnsupportedOperationException.class, day::clear);
        assertThrows(UnsupportedOperationException.class,
                () -> day.get(new TimeOfDay(13, 0)).clear());
        assertThrows(UnsupportedOperationException.class,
                () -> timetable.getTrainingSessionsForDayAndTime(
                        DayOfWeek.MONDAY, new TimeOfDay(13, 0)).clear());
        assertEquals(1, timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)).size());
    }

    @Test
    void dayViewReflectsAddedSessions() {
        NavigableMap<TimeOfDay, List<TrainingSession>> day
                = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        TrainingSession first = add(coach, DayOfWeek.MONDAY, 13, 0);
        TrainingSession second = add(coach, DayOfWeek.MONDAY, 13, 0);
        assertEquals(List.of(first, second), day.get(new TimeOfDay(13, 0)));
    }

    @Test
    void countEmptySchedule() {
        assertTrue(timetable.getCountByCoaches().isEmpty());
    }

    @Test
    void countsSingleCoachAcrossWeek() {
        add(coach, DayOfWeek.MONDAY, 13, 0);
        add(coach, DayOfWeek.WEDNESDAY, 14, 0);
        add(coach, DayOfWeek.SUNDAY, 10, 0);
        List<CounterOfTrainings> counts = timetable.getCountByCoaches();
        assertEquals(1, counts.size());
        assertEquals(coach, counts.get(0).getCoach());
        assertEquals(3, counts.get(0).getCount());
    }

    @Test
    void countsAreSortedDescending() {
        Coach second = new Coach("Белов", "Иван", "Петрович");
        Coach third = new Coach("Алексеев", "Олег", "Петрович");
        add(third, DayOfWeek.MONDAY, 9, 0);
        add(second, DayOfWeek.MONDAY, 10, 0);
        add(second, DayOfWeek.TUESDAY, 10, 0);
        add(coach, DayOfWeek.MONDAY, 11, 0);
        add(coach, DayOfWeek.TUESDAY, 11, 0);
        add(coach, DayOfWeek.WEDNESDAY, 11, 0);
        List<CounterOfTrainings> counts = timetable.getCountByCoaches();
        assertEquals(List.of(coach, second, third),
                counts.stream().map(CounterOfTrainings::getCoach).toList());
        assertEquals(List.of(3, 2, 1),
                counts.stream().map(CounterOfTrainings::getCount).toList());
    }

    @Test
    void coachesWithSameFullNameAreMerged() {
        Coach copy = new Coach("Васильев", "Николай", "Сергеевич");
        add(coach, DayOfWeek.MONDAY, 13, 0);
        add(copy, DayOfWeek.TUESDAY, 13, 0);
        assertEquals(1, timetable.getCountByCoaches().size());
        assertEquals(2, timetable.getCountByCoaches().get(0).getCount());
        assertEquals(coach.hashCode(), copy.hashCode());
    }

    @Test
    void equalCountsKeepEveryCoachInFullNameOrder() {
        Coach other = new Coach("Алексеев", "Иван", "Иванович");
        add(coach, DayOfWeek.MONDAY, 13, 0);
        add(other, DayOfWeek.MONDAY, 13, 0);
        assertEquals(List.of(other, coach), timetable.getCountByCoaches().stream()
                .map(CounterOfTrainings::getCoach).toList());
        assertEquals(List.of(1, 1), timetable.getCountByCoaches().stream()
                .map(CounterOfTrainings::getCount).toList());
    }

    @Test
    void differentNamesAndMiddleNamesRemainSeparate() {
        add(coach, DayOfWeek.MONDAY, 13, 0);
        add(new Coach("Васильев", "Иван", "Сергеевич"), DayOfWeek.MONDAY, 13, 0);
        add(new Coach("Васильев", "Николай", "Петрович"), DayOfWeek.MONDAY, 13, 0);
        assertEquals(3, timetable.getCountByCoaches().size());
    }

    @Test
    void countUpdatesAfterAddingSession() {
        add(coach, DayOfWeek.MONDAY, 13, 0);
        assertEquals(1, timetable.getCountByCoaches().get(0).getCount());
        add(coach, DayOfWeek.MONDAY, 14, 0);
        assertEquals(2, timetable.getCountByCoaches().get(0).getCount());
    }

    @Test
    void rejectsNullSessionAndQueryArguments() {
        assertThrows(NullPointerException.class, () -> timetable.addNewTrainingSession(null));
        assertThrows(NullPointerException.class, () -> timetable.getTrainingSessionsForDay(null));
        assertThrows(NullPointerException.class, () -> timetable.getTrainingSessionsForDayAndTime(
                null, new TimeOfDay(13, 0)));
        assertThrows(NullPointerException.class, () -> timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, null));
    }
}
