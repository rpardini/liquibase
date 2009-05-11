package liquibase.sqlgenerator;

import liquibase.database.Database;
import liquibase.database.MaxDBDatabase;
import liquibase.database.structure.Column;
import liquibase.database.structure.Table;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.AddDefaultValueStatement;

public class AddDefaultValueGeneratorMaxDB extends AddDefaultValueGenerator {
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    public boolean supports(AddDefaultValueStatement statement, Database database) {
        return database instanceof MaxDBDatabase;
    }

    public Sql[] generateSql(AddDefaultValueStatement statement, Database database) {
        return new Sql[]{
                new UnparsedSql("ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " COLUMN  " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " ADD DEFAULT " + database.convertJavaObjectToString(statement.getDefaultValue()),
                        new Column()
                                .setTable(new Table(statement.getTableName()).setSchema(statement.getSchemaName()))
                                .setName(statement.getColumnName()))
        };
    }
}