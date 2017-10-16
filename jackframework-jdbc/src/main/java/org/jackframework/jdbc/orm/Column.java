package org.jackframework.jdbc.orm;

public class Column {

    protected String  columnName;
    protected int     sqlType;
    protected boolean isPrimaryKey;
    protected Table   declaredTable;

    public Column(String columnName, int sqlType, boolean isPrimaryKey, Table declaredTable) {
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.isPrimaryKey = isPrimaryKey;
        this.declaredTable = declaredTable;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getSqlType() {
        return sqlType;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public Table getDeclaredTable() {
        return declaredTable;
    }

}