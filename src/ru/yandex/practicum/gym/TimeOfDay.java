package ru.yandex.practicum.gym;

import java.util.Objects;

public class TimeOfDay implements Comparable<TimeOfDay> {
    private final int hours;
    private final int minutes;

    public TimeOfDay(int hours, int minutes) {
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Hours: 0..23; minutes: 0..59");
        }
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public int compareTo(TimeOfDay other) {
        int result = Integer.compare(hours, other.hours);
        return result != 0 ? result : Integer.compare(minutes, other.minutes);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        TimeOfDay time = (TimeOfDay) other;
        return hours == time.hours && minutes == time.minutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }
}
