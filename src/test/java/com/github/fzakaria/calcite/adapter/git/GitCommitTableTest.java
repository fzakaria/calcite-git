package com.github.fzakaria.calcite.adapter.git;

import com.github.fzakaria.calcite.adapter.git.pojo.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static com.github.fzakaria.calcite.adapter.git.ResultSetConsumers.commits;
import static org.assertj.core.api.Assertions.assertThat;

public class GitCommitTableTest {

    private Git git;
    private Path directory;

    @BeforeEach
    public void beforeEach(@TempDir Path tempFolder) throws GitAPIException {
        git = Git.init().setDirectory(tempFolder.toFile()).call();
        directory = tempFolder;
    }

    @Test
    public void testMultipleParents() throws Exception {
        final String originalBranch = git.getRepository().getBranch();
        // create a file
        Files.write(directory.resolve("file_a"), "Hello World".getBytes(StandardCharsets.UTF_8));
        // create a commit inside
        git.commit().setAuthor("Bob Joe", "bob@joe.com").setMessage("My first commit on master.").call();
        // create a feature branch
        git.checkout().setCreateBranch(true).setName("feature-1").call();
        // create a different file
        Files.write(directory.resolve("file_b"), "Hello Other World".getBytes(StandardCharsets.UTF_8));
        // create a commit inside
        git.commit().setAuthor("Bob Joe", "bob@joe.com").setMessage("My first commit on feature branch.").call();
        // go back to the original branch
        git.checkout().setName(originalBranch).call();
        // merge -- this should create a commit with two parents
        // make sure to disable FF
        git.merge().setFastForward(MergeCommand.FastForwardMode.NO_FF).include(git.getRepository().resolve("feature-1")).call();

        final Properties properties = new Properties();
        properties.setProperty("directory", directory.toString());
        try (Connection connection = DriverManager.getConnection("jdbc:git:", properties)) {
            try (Statement statement = connection.createStatement()) {
                final ResultSet resultSet = statement.executeQuery("select * from commits limit 10");
                final List<Commit> commits = commits(resultSet);
                assertThat(commits).anySatisfy(commit -> assertThat(commit.getParents()).hasSize(2));
            }
        }
    }
}
