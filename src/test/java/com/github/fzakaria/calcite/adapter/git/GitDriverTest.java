package com.github.fzakaria.calcite.adapter.git;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GitDriverTest {

    @Test
    public void testVanityDriver() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            //do nothing
        }
    }

}
