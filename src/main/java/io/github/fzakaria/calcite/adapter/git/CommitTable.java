package io.github.fzakaria.calcite.adapter.git;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A table that essentially equates to `git log`
 */
public class CommitTable extends AbstractTable implements ScannableTable {

    private final Git git;

    public CommitTable(Git git) {
        this.git = git;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return typeFactory.builder()
                .add("id", SqlTypeName.VARCHAR)
                .add("message", SqlTypeName.VARCHAR)
                .add("summary", SqlTypeName.VARCHAR)
                .add("author_name", SqlTypeName.VARCHAR)
                .add("author_email", SqlTypeName.VARCHAR)
                .add("author_date", SqlTypeName.TIMESTAMP)
                .add("committer_name", SqlTypeName.VARCHAR)
                .add("committer_email", SqlTypeName.VARCHAR)
                .add("committer_date", SqlTypeName.TIMESTAMP)
                .add("parents", typeFactory.createArrayType(
                        typeFactory.createSqlType(SqlTypeName.VARCHAR), -1)
                )
                .build();
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        try {
            Iterable<Object[]> commits =
                    StreamSupport.stream(git.log().all().call().spliterator(), false)
                            .map(commit -> new Object[]{
                                    commit.getId().getName(),
                                    commit.getShortMessage(),
                                    commit.getFullMessage(),
                                    commit.getAuthorIdent().getName(),
                                    commit.getAuthorIdent().getEmailAddress(),
                                    commit.getAuthorIdent().getWhen().getTime(),
                                    commit.getCommitterIdent().getName(),
                                    commit.getCommitterIdent().getEmailAddress(),
                                    commit.getCommitterIdent().getWhen().getTime(),
                                    Stream.of(commit.getParents()).map(AnyObjectId::getName).toArray()
                            }).collect(Collectors.toList());
            return Linq4j.asEnumerable(commits);
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
