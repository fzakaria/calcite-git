package io.github.fzakaria.calcite.adapter.git;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static io.github.fzakaria.calcite.adapter.git.ResultSetConsumers.strColumn;
import static org.assertj.core.api.Assertions.assertThat;

public class GitSchemaTest {

    @Test
    public void testListSchemas() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getSchemas();
            List<String> schemas = strColumn(resultSet, "TABLE_SCHEM");
            assertThat(schemas).contains("git");
        }
    }

    @Test
    public void testListTables() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getTables(null, "git", null, null);
            List<String> tables = strColumn(resultSet, "TABLE_NAME");
            assertThat(tables).contains("COMMITS");
        }
    }

}
