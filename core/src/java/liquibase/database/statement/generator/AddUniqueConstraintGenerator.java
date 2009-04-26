package liquibase.database.statement.generator;

import liquibase.database.statement.AddUniqueConstraintStatement;
import liquibase.database.statement.syntax.Sql;
import liquibase.database.statement.syntax.UnparsedSql;
import liquibase.database.*;
import liquibase.exception.JDBCException;
import liquibase.util.StringUtils;

public class AddUniqueConstraintGenerator implements SqlGenerator<AddUniqueConstraintStatement> {
    public int getSpecializationLevel() {
        return SPECIALIZATION_LEVEL_DEFAULT;
    }

    public boolean isValidGenerator(AddUniqueConstraintStatement statement, Database database) {
        return !(database instanceof SQLiteDatabase);
    }

    public GeneratorValidationErrors validate(AddUniqueConstraintStatement addUniqueConstraintStatement, Database database) {
        return new GeneratorValidationErrors();
    }

    public Sql[] generateSql(AddUniqueConstraintStatement statement, Database database) throws JDBCException {
      String sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ADD CONSTRAINT " + database.escapeConstraintName(statement.getConstraintName()) + " UNIQUE (" + database.escapeColumnNameList(statement.getColumnNames()) + ")";
    	if (database instanceof InformixDatabase) {
    		sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ADD CONSTRAINT UNIQUE (" + database.escapeColumnNameList(statement.getColumnNames()) + ") CONSTRAINT " + database.escapeConstraintName(statement.getConstraintName());
    	}

        if (StringUtils.trimToNull(statement.getTablespace()) != null && database.supportsTablespaces()) {
            if (database instanceof MSSQLDatabase) {
                sql += " ON " + statement.getTablespace();
            } else if (database instanceof DB2Database
                || database instanceof SybaseASADatabase
                || database instanceof InformixDatabase) {
                ; //not supported
            } else {
                sql += " USING INDEX TABLESPACE " + statement.getTablespace();
            }
        }

        return new Sql[] {
                new UnparsedSql(sql)
        };

    }
}
