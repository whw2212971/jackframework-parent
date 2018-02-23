package org.jackframework.jdbc.orm;

import org.jackframework.common.CharsWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

    protected String              tableName;
    protected List<Column>        columns;
    protected Map<String, Column> columnMap;

    protected String deleteByIdSql;
    protected String deleteByWherePrefix;
    protected String updateByWherePrefix;

    public Table(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
        this.columnMap = createColumnMap(columns);
        this.deleteByIdSql = buildDeleteByIdSql(this);
        this.deleteByWherePrefix = buildDeleteByWherePrefix(this);
        this.updateByWherePrefix = buildDeleteByWherePrefix(this);
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

    public String getDeleteByIdSql() {
        return deleteByIdSql;
    }

    public String getDeleteByWherePrefix() {
        return deleteByWherePrefix;
    }

    public String getUpdateByWherePrefix() {
        return updateByWherePrefix;
    }

    protected static String buildDeleteByIdSql(Table table) {
        return new CharsWriter().append("DELETE FROM ")
                .append(table.getTableName())
                .append(" WHERE ").append(table.getColumn(0).getColumnName())
                .append("=?").closeToString();
    }

    protected static String buildDeleteByWherePrefix(Table table) {
        return new CharsWriter().append("DELETE FROM ")
                .append(table.getTableName()).append(' ').closeToString();
    }

    protected static String buildUpdateByWherePrefix(Table table) {
        return new CharsWriter().append("UPDATE ")
                .append(table.getTableName()).append(" ").closeToString();
    }

}