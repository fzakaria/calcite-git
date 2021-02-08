package io.github.fzakaria.calcite.adapter.git;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class Commit {

    private final String id;
    private final String message;
    private final String summary;
    private final Person author;
    private final Person committer;
    private final Set<String> parents;

    public static Commit fromResultSet(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String message = resultSet.getString("message");
        String summary = resultSet.getString("summary");
        String authorName = resultSet.getString("author_name");
        String authorEmail = resultSet.getString("author_email");
        Instant authorDate = resultSet.getTimestamp("author_date").toInstant();
        String committeName = resultSet.getString("committer_name");
        String committerEmail = resultSet.getString("committer_email");
        Instant committerDate = resultSet.getTimestamp("committer_date").toInstant();
        Object[] parents = (Object[]) resultSet.getArray("parents").getArray();
        return new Commit(
                id,
                message,
                summary,
                new Person(authorName, authorEmail, authorDate),
                new Person(committeName, committerEmail, committerDate),
                Sets.newHashSet(Arrays.copyOf(parents, parents.length, String[].class))
        );
    }

    public Commit(String id, String message, String summary,
                  Person author, Person committer, Set<String> parents) {
        this.id = id;
        this.message = message;
        this.summary = summary;
        this.author = author;
        this.committer = committer;
        this.parents = parents;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSummary() {
        return summary;
    }

    public Person getAuthor() {
        return author;
    }

    public Person getCommitter() {
        return committer;
    }

    public Set<String> getParents() {
        return parents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(id, commit.id) &&
                Objects.equals(message, commit.message) &&
                Objects.equals(summary, commit.summary) &&
                Objects.equals(author, commit.author) &&
                Objects.equals(committer, commit.committer) &&
                Objects.equals(parents, commit.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, summary, author, committer, parents);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("message", message)
                .add("summary", summary)
                .add("author", author)
                .add("committer", committer)
                .add("parents", parents)
                .toString();
    }
}
