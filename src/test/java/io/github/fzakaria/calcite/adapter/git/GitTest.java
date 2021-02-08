package io.github.fzakaria.calcite.adapter.git;

import io.github.fzakaria.calcite.adapter.git.pojo.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static io.github.fzakaria.calcite.adapter.git.ResultSetConsumers.commits;
import static org.assertj.core.api.Assertions.assertThat;

public class GitTest {

    private Git git;

    @BeforeEach
    public void beforeEach(@TempDir Path tempFolder) throws GitAPIException {
        git = Git.init().setDirectory(tempFolder.toFile()).call();
    }

    @Test
    public void testBasicSelectSQL() throws SQLException {
        Properties info = new Properties();
        try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
            try (Statement statement = connection.createStatement()) {
                final ResultSet resultSet = statement.executeQuery("select * from commits limit 10");
                final List<Commit> commits = commits(resultSet);
                assertThat(commits).isNotEmpty();
            }
        }
    }

}
