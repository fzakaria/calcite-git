package io.github.fzakaria.calcite.adapter.git;

import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;

/**
 * A combination of a person identity and time in Git.
 */
public class Person {

    private final String name;
    private final String email;
    private final Instant when;

    public Person(String name, String email, Instant when) {
        this.name = name;
        this.email = email;
        this.when = when;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Instant getWhen() {
        return when;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(email, person.email) &&
                Objects.equals(when, person.when);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, when);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("email", email)
                .add("when", when)
                .toString();
    }
}
