package liquibase.database.statement.generator;

import liquibase.database.statement.AddDefaultValueStatement;
import liquibase.database.statement.syntax.Sql;
import liquibase.database.statement.syntax.UnparsedSql;
import liquibase.database.Database;
import liquibase.exception.JDBCException;

public class AddDefaultValueGenerator implements SqlGenerator<AddDefaultValueStatement> {
    public int getSpecializationLevel() {
        return SPECIALIZATION_LEVEL_DEFAULT;
    }

    public boolean isValidGenerator(AddDefaultValueStatement statement, Database database) {
        return true;
    }

    public GeneratorValidationErrors validate(AddDefaultValueStatement addDefaultValueStatement, Database database) {
        return new GeneratorValidationErrors();
    }

    public Sql[] generateSql(AddDefaultValueStatement statement, Database database) throws JDBCException {
        return new Sql[] {
                new UnparsedSql("ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ALTER COLUMN  " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " SET DEFAULT " + database.convertJavaObjectToString(statement.getDefaultValue()))
        };
    }
}
