package org.jackframework.jdbc.orm;


import java.util.Arrays;
import java.util.List;

public class ClassTable {

    protected Table         table;
    protected Class<?>      dataType;
    protected FieldColumn[] fieldColumns;

    public ClassTable(Table table, Class<?> dataType, FieldColumn[] fieldColumns) {
        this.table = table;
        this.dataType = dataType;
        this.fieldColumns = fieldColumns;
    }

    public Table getTable() {
        return table;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public int getFieldColumnsCount() {
        return fieldColumns.length;
    }

    public FieldColumn getFieldColumn(int index) {
        return fieldColumns[index];
    }

    public List<FieldColumn> getFieldColumns() {
        return Arrays.asList(fieldColumns);
    }

}