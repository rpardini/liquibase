package liquibase.database.statement.generator;

import liquibase.database.Database;
import liquibase.database.InformixDatabase;
import liquibase.database.SQLiteDatabase;
import liquibase.database.statement.syntax.Sql;
import liquibase.database.statement.syntax.UnparsedSql;
import liquibase.database.statement.AddForeignKeyConstraintStatement;
import liquibase.exception.StatementNotSupportedOnDatabaseException;
import liquibase.exception.JDBCException;

import java.sql.DatabaseMetaData;

public class AddForeignKeyConstraintGenerator implements SqlGenerator<AddForeignKeyConstraintStatement> {
    public int getSpecializationLevel() {
        return SPECIALIZATION_LEVEL_DEFAULT;
    }

    public boolean isValidGenerator(AddForeignKeyConstraintStatement statement, Database database) {
        return (!(database instanceof SQLiteDatabase));
    }

    public GeneratorValidationErrors validate(AddForeignKeyConstraintStatement addForeignKeyConstraintStatement, Database database) {
        return new GeneratorValidationErrors();
    }

    public Sql[] generateSql(AddForeignKeyConstraintStatement statement, Database database) throws JDBCException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
        	.append(database.escapeTableName(statement.getBaseTableSchemaName(), statement.getBaseTableName()))
        	.append(" ADD CONSTRAINT ");
        if (!(database instanceof InformixDatabase)) {
        	sb.append(database.escapeConstraintName(statement.getConstraintName()));
        }
        sb.append(" FOREIGN KEY (")
        	.append(database.escapeColumnNameList(statement.getBaseColumnNames()))
        	.append(") REFERENCES ")
        	.append(database.escapeTableName(statement.getReferencedTableSchemaName(), statement.getReferencedTableName()))
        	.append("(")
        	.append(database.escapeColumnNameList(statement.getReferencedColumnNames()))
        	.append(")");

        if (statement.getUpdateRule() != null) {
            switch (statement.getUpdateRule()) {
                case DatabaseMetaData.importedKeyCascade:
                    sb.append(" ON UPDATE CASCADE");
                    break;
                case DatabaseMetaData.importedKeySetNull:
                    sb.append(" ON UPDATE SET NULL");
                    break;
                case DatabaseMetaData.importedKeySetDefault:
                    sb.append(" ON UPDATE SET DEFAULT");
                    break;
                case DatabaseMetaData.importedKeyRestrict:
                    if (database.supportsRestrictForeignKeys()) {
                        sb.append(" ON UPDATE RESTRICT");
                    }
                    break;
                case DatabaseMetaData.importedKeyNoAction:
                    //don't do anything
//                    sql += " ON UPDATE NO ACTION";
                    break;
                default:
                    break;
            }
        }
        if (statement.getDeleteRule() != null) {
            switch (statement.getDeleteRule()) {
                case DatabaseMetaData.importedKeyCascade:
                    sb.append(" ON DELETE CASCADE");
                    break;
                case DatabaseMetaData.importedKeySetNull:
                	sb.append(" ON DELETE SET NULL");
                    break;
                case DatabaseMetaData.importedKeySetDefault:
                	sb.append(" ON DELETE SET DEFAULT");
                    break;
                case DatabaseMetaData.importedKeyRestrict:
                    if (database.supportsRestrictForeignKeys()) {
                    	sb.append(" ON DELETE RESTRICT");
                    }
                    break;
                case DatabaseMetaData.importedKeyNoAction:
                    //don't do anything
//                    sql += " ON DELETE NO ACTION";
                    break;
                default:
                    break;
            }
        }

        if (statement.isDeferrable() || statement.isInitiallyDeferred()) {
            if (!database.supportsInitiallyDeferrableColumns()) {
                throw new StatementNotSupportedOnDatabaseException("Database does not support deferrable foreign keys", statement, database);
            }

            if (statement.isDeferrable()) {
            	sb.append(" DEFERRABLE");
            }

            if (statement.isInitiallyDeferred()) {
            	sb.append(" INITIALLY DEFERRED");
            }
        }

        if (database instanceof InformixDatabase) {
        	sb.append(" CONSTRAINT ");
        	sb.append(database.escapeConstraintName(statement.getConstraintName()));
        }

        return new Sql[] { new UnparsedSql(sb.toString()) };
    }
}
