package com.github.fzakaria.calcite.adapter.git;

import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.schema.SchemaPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class GitDriver extends Driver {

    private static final Logger LOG = LoggerFactory.getLogger(GitDriver.class);

    private static class GitDriverVersion extends DriverVersion {
        /** Creates a SplunkDriverVersion. */
        GitDriverVersion() {
            super(
                    "Calcite JDBC Driver for Git",
                    "0.1",
                    "Calcite-Git",
                    "0.1",
                    true,
                    0,
                    1,
                    0,
                    1);
        }
    }

    static {
        new GitDriver().register();
    }

    @Override
    protected String getConnectStringPrefix() {
        return "jdbc:git:";
    }

    @Override
    protected DriverVersion createDriverVersion() {
        return new GitDriverVersion();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Connection connection = super.connect(url, info);
        CalciteConnection calciteConnection = (CalciteConnection) connection;
        final SchemaPlus rootSchema = calciteConnection.getRootSchema();

        String directory = info.getProperty("directory");

        // if no directory is provided, default to current working directory
        if (directory == null) {
            directory = System.getProperty("user.dir");
            LOG.warn("No directory provided, defaulting to {}", directory);
        }

        rootSchema.add("git", new GitSchema(new File(directory)));

        // set the default schema to git
        calciteConnection.setSchema("git");

        return connection;
    }

}
