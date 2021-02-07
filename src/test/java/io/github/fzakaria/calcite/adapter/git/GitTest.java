package io.github.fzakaria.calcite.adapter.git;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class GitTest {

    @BeforeAll
    public static void beforeAll() {
        loadDriverClass();
    }

    @Test
    public void testVanityDriver() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            //do nothing
        }
    }

    @Test
    public void testListSchemas() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getSchemas();
            List<String> schemas = singleColumn(resultSet, "TABLE_SCHEM");
            assertThat(schemas).contains("git");
        }
    }

    @Test
    public void testListTables() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getTables(null, "git", null, null);
            List<String> tables = singleColumn(resultSet, "TABLE_NAME");
            assertThat(tables).contains("commits");
        }
    }

    @Test
    public void testBasicSelectSQL() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            try (Statement statement = connection.createStatement()) {
                final ResultSet resultSet = statement.executeQuery("select * from commits limit 10");
                final List<Commit> commits = allCommitsFromResultSet(resultSet);
                assertThat(commits).isNotEmpty();
            }
        }
    }

    private static List<String> singleColumn(ResultSet resultSet, String column) throws SQLException {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        while (resultSet.next()) {
            builder.add(resultSet.getString(column));
        }
        return builder.build();
    }

    private static List<Commit> allCommitsFromResultSet(ResultSet resultSet) throws SQLException {
        ImmutableList.Builder<Commit> builder = ImmutableList.builder();
        while (resultSet.next()) {
            builder.add(Commit.fromResultSet(resultSet));
        }
        return builder.build();
    }

    private static void loadDriverClass() {
        try {
            Class.forName("io.github.fzakaria.calcite.adapter.git.GitDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("driver not found", e);
        }
    }
}
