package ru.yandex.practicum.gym;

import java.util.Objects;

public class TrainingSession {
    private final Group group;
    private final Coach coach;
    private final DayOfWeek dayOfWeek;
    private final TimeOfDay timeOfDay;

    public TrainingSession(Group group, Coach coach, DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        this.group = Objects.requireNonNull(group, "group");
        this.coach = Objects.requireNonNull(coach, "coach");
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        this.timeOfDay = Objects.requireNonNull(timeOfDay, "timeOfDay");
    }

    public Group getGroup() { return group; }
    public Coach getCoach() { return coach; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public TimeOfDay getTimeOfDay() { return timeOfDay; }
}
