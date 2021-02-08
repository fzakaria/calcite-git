package io.github.fzakaria.calcite.adapter.git;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
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
        final RelDataType personStruct = typeFactory.createStructType(
                ImmutableList.of(
                        typeFactory.createSqlType(SqlTypeName.VARCHAR),
                        typeFactory.createSqlType(SqlTypeName.VARCHAR),
                        typeFactory.createSqlType(SqlTypeName.TIMESTAMP)
                ),
                ImmutableList.of("NAME", "EMAIL", "CREATED_AT")
        );
        return typeFactory.builder()
                .add("ID", SqlTypeName.VARCHAR)
                .add("MESSAGE", SqlTypeName.VARCHAR)
                .add("SUMMARY", SqlTypeName.VARCHAR)
                .add("AUTHOR", personStruct)
                .add("COMMITTER", personStruct)
                .add("PARENTS", typeFactory.createMultisetType(
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
                                    new Object[] {
                                        commit.getAuthorIdent().getName(),
                                        commit.getAuthorIdent().getEmailAddress(),
                                        commit.getAuthorIdent().getWhen().toInstant().toEpochMilli()
                                    },
                                    new Object[] {
                                        commit.getCommitterIdent().getName(),
                                        commit.getCommitterIdent().getEmailAddress(),
                                        commit.getCommitterIdent().getWhen().toInstant().toEpochMilli()
                                    },
                                    Stream.of(commit.getParents()).map(AnyObjectId::getName).toArray()
                            }).collect(Collectors.toList());
            return Linq4j.asEnumerable(commits);
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
