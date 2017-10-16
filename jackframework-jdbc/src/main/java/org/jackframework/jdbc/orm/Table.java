package org.jackframework.jdbc.orm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

    protected String              tableName;
    protected List<Column>        columns;
    protected Map<String, Column> columnMap;

    public Table(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
        this.columnMap = createColumnMap(columns);
    }

    protected Map<String, Column> createColumnMap(List<Column> columns) {
        Map<String, Column> columnMap = new HashMap<String, Column>();
        for (Column column : columns) {
            columnMap.put(column.getColumnName(), column);
        }
        return columnMap;
    }

    public String getTableName() {
        return tableName;
    }

    public int getColumnsCount() {
        return columns.size();
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

    public Column getColumn(String name) {
        return columnMap.get(name);
    }

}