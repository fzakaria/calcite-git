package io.github.fzakaria.calcite.adapter.git;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ResultSetConsumers {

    private ResultSetConsumers() {
    }

    /**
     * Fetch a single column and it's mapping.
     */
    public static List<String> strColumn(ResultSet resultSet, String column) throws SQLException {
        return columns(resultSet, ImmutableMap.of(column, String.class))
                .stream().map(Object::toString).collect(Collectors.toList());
    }

    /**
     * Fetch a single column and it's mapping.
     */
    public static List<Object> column(ResultSet resultSet, String column, Class<?> type) throws SQLException {
        return columns(resultSet, ImmutableMap.of(column, type));
    }

    /**
     * A simple method that returns a the desired columns and their type mapping.
     */
    public static List<Object> columns(ResultSet resultSet, Map<String, Class<?>> columns) throws SQLException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        while (resultSet.next()) {
            for (Map.Entry<String, Class<?>> column: columns.entrySet()) {
                final Class<?> clazz = column.getValue();
                if (String.class.isAssignableFrom(clazz)) {
                    builder.add(resultSet.getString(column.getKey()));
                } else if (Integer.class.isAssignableFrom(clazz)){
                    builder.add(resultSet.getInt(column.getKey()));
                } else {
                    builder.add(resultSet.getObject(column.getKey(), column.getValue()));
                }
            }
        }
        return builder.build();
    }

    public static List<Commit> commits(ResultSet resultSet) throws SQLException {
        ImmutableList.Builder<Commit> builder = ImmutableList.builder();
        while (resultSet.next()) {
            builder.add(Commit.fromResultSet(resultSet));
        }
        return builder.build();
    }

}
