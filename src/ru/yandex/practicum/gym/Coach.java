package ru.yandex.practicum.gym;

import java.util.Objects;

public class Coach {
    private final String surname;
    private final String name;
    private final String middleName;

    public Coach(String surname, String name, String middleName) {
        this.surname = Objects.requireNonNull(surname, "surname");
        this.name = Objects.requireNonNull(name, "name");
        this.middleName = Objects.requireNonNull(middleName, "middleName");
    }

    public String getSurname() { return surname; }
    public String getName() { return name; }
    public String getMiddleName() { return middleName; }

    @Override
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        Coach coach = (Coach) other;
        return surname.equals(coach.surname)
                && name.equals(coach.name) && middleName.equals(coach.middleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname, name, middleName);
    }

    @Override
    public String toString() {
        return surname + " " + name + " " + middleName;
    }
}
