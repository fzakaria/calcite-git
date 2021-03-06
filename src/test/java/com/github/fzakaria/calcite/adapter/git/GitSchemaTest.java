package com.github.fzakaria.calcite.adapter.git;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class GitSchemaTest {

    @Test
    public void testListSchemas() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getSchemas();
            List<String> schemas = ResultSetConsumers.strColumn(resultSet, "TABLE_SCHEM");
            assertThat(schemas).contains("git");
        }
    }

    @Test
    public void testListTables() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getTables(null, "git", null, null);
            List<String> tables = ResultSetConsumers.strColumn(resultSet, "TABLE_NAME");
            assertThat(tables).contains("COMMITS");
        }
    }

    @Test
    public void testTableMetadata() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            ResultSet resultSet = connection.getMetaData().getColumns(null, "git", "COMMITS", null);
            List<Object> columns = ResultSetConsumers.columns(resultSet, ImmutableMap.of("COLUMN_NAME", String.class, "TYPE_NAME", String.class));
            assertThat(columns).contains("ID", "MESSAGE", "SUMMARY", "AUTHOR", "COMMITTER", "PARENTS");
            assertThat(columns).contains("RecordType(VARCHAR NOT NULL NAME, VARCHAR NOT NULL EMAIL, TIMESTAMP(0) NOT NULL CREATED_AT) NOT NULL");
        }
    }

}
