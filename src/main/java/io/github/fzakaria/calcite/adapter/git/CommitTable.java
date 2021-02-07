package io.github.fzakaria.calcite.adapter.git;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * A table that essentially equates to `git log`
 */
public class CommitTable extends AbstractTable {

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return typeFactory.builder()
                .add("i", SqlTypeName.INTEGER)
                .add("j", SqlTypeName.VARCHAR)
                .build();
    }
}
