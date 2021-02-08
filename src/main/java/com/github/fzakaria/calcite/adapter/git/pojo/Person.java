package com.github.fzakaria.calcite.adapter.git.pojo;

import com.google.common.base.MoreObjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * A combination of a person identity and time in Git.
 */
public class Person {

    private final String name;
    private final String email;
    private final Instant date;

    /**
     * Create a {@link Person} from a {@link ResultSet}
     */
    public static Person fromSqlStruct(Struct struct) throws SQLException {
        Object[] attributes = struct.getAttributes();
        String name = (String) attributes[0];
        String email = (String) attributes[1];
        Instant date = ((Timestamp) attributes[2]).toInstant();
        return new Person(name, email, date);
    }

    public Person(String name, String email, Instant date) {
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Instant getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(email, person.email) &&
                Objects.equals(date, person.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, date);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("email", email)
                .add("when", date)
                .toString();
    }
}
