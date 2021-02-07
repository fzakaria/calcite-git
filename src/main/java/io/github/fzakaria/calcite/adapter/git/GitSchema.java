package io.github.fzakaria.calcite.adapter.git;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import static java.lang.String.format;

/**
 * Schema mapped onto Git commits. Each table in the schema
 * represents different Git information.
 */
public class GitSchema extends AbstractSchema {

    private final Git git;

    /**
     * Create a new {@link GitSchema} pointing at the already cloned Git
     * directory.
     * @param dir The git directory.
     */
    public GitSchema(File dir) {
        super();

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(format("%s must be a directory", dir));
        }
        try {
            git = Git.open(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        return ImmutableMap.of("COMMITS", new CommitTable(git));
    }
}
