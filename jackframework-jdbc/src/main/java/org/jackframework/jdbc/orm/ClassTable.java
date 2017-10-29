package org.jackframework.jdbc.orm;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassTable {

    protected Table                    table;
    protected Class<?>                 dataType;
    protected FieldColumn[]            fieldColumns;
    protected Map<String, FieldColumn> fieldColumnMap;

    public ClassTable(Table table, Class<?> dataType, FieldColumn[] fieldColumns) {
        this.table = table;
        this.dataType = dataType;
        this.fieldColumns = fieldColumns;
        this.fieldColumnMap = new HashMap<String, FieldColumn>();
        for (FieldColumn fieldColumn : fieldColumns) {
            fieldColumnMap.put(fieldColumn.getFieldName(), fieldColumn);
            fieldColumnMap.put(fieldColumn.getColumnName(), fieldColumn);
        }
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

    public FieldColumn getPrimaryFieldColumn() {
        return fieldColumns[0];
    }

    public FieldColumn getFieldColumn(int index) {
        return fieldColumns[index];
    }

    public FieldColumn getFieldColumn(String name) {
        return fieldColumnMap.get(name);
    }

    public List<FieldColumn> getFieldColumns() {
        return Arrays.asList(fieldColumns);
    }

}