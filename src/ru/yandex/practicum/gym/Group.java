package ru.yandex.practicum.gym;

import java.util.Objects;

public class Group {
    private final String title;
    private final Age age;
    private final int duration;

    public Group(String title, Age age, int duration) {
        this.title = Objects.requireNonNull(title, "title");
        this.age = Objects.requireNonNull(age, "age");
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public Age getAge() {
        return age;
    }

    public int getDuration() {
        return duration;
    }
}
