package ru.yandex.practicum.gym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class Timetable {
    private final Map<DayOfWeek, NavigableMap<TimeOfDay, List<TrainingSession>>> timetable
            = new HashMap<>();

    public Timetable() {
        for (DayOfWeek day : DayOfWeek.values()) {
            timetable.put(day, new TreeMap<>());
        }
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        Objects.requireNonNull(trainingSession, "trainingSession");
        NavigableMap<TimeOfDay, List<TrainingSession>> day
                = timetable.get(trainingSession.getDayOfWeek());
        TimeOfDay time = trainingSession.getTimeOfDay();
        // Copy only when adding: queries never copy or sort the stored data.
        List<TrainingSession> sessions = new ArrayList<>(
                day.getOrDefault(time, Collections.emptyList()));
        sessions.add(trainingSession);
        day.put(time, Collections.unmodifiableList(sessions));
    }

    /** O(1): returns a read-only view, with times in ascending order. */
    public NavigableMap<TimeOfDay, List<TrainingSession>> getTrainingSessionsForDay(
            DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        return Collections.unmodifiableNavigableMap(timetable.get(dayOfWeek));
    }

    /** O(log m), where m is the number of distinct start times for this day. */
    public List<TrainingSession> getTrainingSessionsForDayAndTime(
            DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        Objects.requireNonNull(timeOfDay, "timeOfDay");
        return timetable.get(dayOfWeek).getOrDefault(timeOfDay, Collections.emptyList());
    }

    public List<CounterOfTrainings> getCountByCoaches() {
        Map<Coach, Integer> counts = new HashMap<>();
        for (NavigableMap<TimeOfDay, List<TrainingSession>> day : timetable.values()) {
            for (List<TrainingSession> sessions : day.values()) {
                for (TrainingSession session : sessions) {
                    counts.merge(session.getCoach(), 1, Integer::sum);
                }
            }
        }
        List<CounterOfTrainings> result = new ArrayList<>();
        counts.forEach((coach, count) -> result.add(new CounterOfTrainings(coach, count)));
        // Equal counts are ordered by full name for a reproducible result.
        result.sort(Comparator.comparingInt(CounterOfTrainings::getCount).reversed()
                .thenComparing(counter -> counter.getCoach().getSurname())
                .thenComparing(counter -> counter.getCoach().getName())
                .thenComparing(counter -> counter.getCoach().getMiddleName()));
        return Collections.unmodifiableList(result);
    }
}
