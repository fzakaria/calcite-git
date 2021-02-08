package com.github.fzakaria.calcite.adapter.git;


import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.io.File;
import java.util.Map;

/**
 * Factory that creates a {@link GitSchemaFactory}.
 *
 * <p>Allows a custom schema to be included in a <code><i>model</i>.json</code>
 * file.
 */
public class GitSchemaFactory implements SchemaFactory {

    private GitSchemaFactory() {
    }

    @Override public Schema create(SchemaPlus parentSchema, String name,
                                   Map<String, Object> operand) {

        final String directory = (String) operand.get("directory");
        final File directoryFile = new File(directory);
        return new GitSchema(
                directoryFile
        );
    }
}
