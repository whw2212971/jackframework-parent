package org.jackframework.jdbc.parts;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.core.Excludes;
import org.jackframework.jdbc.core.Includes;
import org.jackframework.jdbc.dialect.InsertChannel;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.orm.ClassTable;
import org.jackframework.jdbc.orm.FieldColumn;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataAccessChannel {

    protected DataSource    dataSource;
    protected InsertChannel insertChannel;
    protected ClassTable    classTable;

    protected String deleteByIdSql;
    protected String deleteByWherePrefix;

    protected String updateAllSql;
    protected String updateOptimizePrefix;
    protected String updateByWherePrefix;

    public void insert(Object dataObject) {
        insertChannel.insert(dataObject);
    }

    public void insertList(List<?> dataList) {
        insertChannel.insertList(dataList);
    }

    public int deleteById(Object id) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(deleteByIdSql);
            JdbcUtils.setStatementArg(statement, 1, id);
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    public int deleteByWhere(String whereClause, Object[] statementArgs) {
        return executeUpdate(deleteByWherePrefix, whereClause, statementArgs);
    }

    public int updateAll(Object dataObject) {
        FieldColumn idColumn = classTable.getFieldColumn(0);
        Object      id       = idColumn.getValue(dataObject);
        if (id == null) {
            return 0;
        }
        Connection        connection = null;
        PreparedStatement statement  = null;
        ClassTable        classTable = this.classTable;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(updateAllSql);
            int fieldsCount = classTable.getFieldColumnsCount();
            for (int i = 1; i < fieldsCount; i++) {
                classTable.getFieldColumn(i).setStatementValue(statement, i, dataObject);
            }
            idColumn.getStatementSetter().setValue(statement, fieldsCount, id);
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    public int updateByWhere(String updateClause, Object[] statementArgs) {
        return executeUpdate(updateByWherePrefix, updateClause, statementArgs);
    }

    public int updateOptimized(Object dataObject, Set<String> forceUpdateFields) {
        FieldColumn idColumn = classTable.getFieldColumn(0);
        Object      id       = idColumn.getValue(dataObject);
        if (id == null) {
            return 0;
        }
        ClassTable  classTable  = this.classTable;
        CharsWriter cbuf        = new CharsWriter().append(updateOptimizePrefix);
        int         fieldsCount = classTable.getFieldColumnsCount();
        boolean     isFirst     = true;
        for (int i = 0; i < fieldsCount; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (fieldColumn.getValue(dataObject) != null || contains(forceUpdateFields, fieldColumn)) {
                if (isFirst) {
                    cbuf.write("set ");
                    isFirst = false;
                } else {
                    cbuf.write(',');
                }
                cbuf.append(fieldColumn.getColumn().getColumnName()).append("=?");
            }
        }
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(cbuf.closeThen().toString());
            int index = 1;
            for (int i = 0; i < fieldsCount; i++) {
                FieldColumn fieldColumn = classTable.getFieldColumn(i);
                if (fieldColumn.getValue(dataObject) != null) {
                    fieldColumn.setStatementValue(statement, index++, dataObject);
                } else if (contains(forceUpdateFields, fieldColumn)) {
                    statement.setString(index++, null);
                }
            }
            idColumn.getStatementSetter().setValue(statement, index, id);
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    public <T> T findById(Object id, ResultHandler<T> resultHandler) {
        List<FieldColumn> fieldColumns  = classTable.getFieldColumns();
        int               length        = fieldColumns.size();
        int[]             resultIndexes = new int[length];
        for (int i = 0; i < length; i++) {
            resultIndexes[i] = i + 1;
        }
        return findById(id, fieldColumns, resultIndexes, resultHandler);
    }

    public <T> T findById(Object id, Includes includes, ResultHandler<T> resultHandler) {
        ClassTable        classTable    = this.classTable;
        int               length        = classTable.getFieldColumnsCount();
        List<FieldColumn> fieldColumns  = new ArrayList<FieldColumn>(length);
        int[]             resultIndexes = new int[length];
        int               index         = 1;
        for (int i = 0; i < length; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (includes.contains(fieldColumn)) {
                fieldColumns.add(fieldColumn);
                resultIndexes[i] = index++;
            } else {
                resultIndexes[i] = -1;
            }
        }
        if (index == 1) {
            throw new CommonDaoException("Could not found any include fields: {}", includes.toIncludesString());
        }
        return findById(id, fieldColumns, resultIndexes, resultHandler);
    }

    public <T> T findById(Object id, Excludes excludes, ResultHandler<T> resultHandler) {
        ClassTable        classTable    = this.classTable;
        int               length        = classTable.getFieldColumnsCount();
        List<FieldColumn> fieldColumns  = new ArrayList<FieldColumn>(length);
        int[]             resultIndexes = new int[length];
        int               index         = 1;
        for (int i = 0; i < length; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (excludes.contains(fieldColumn)) {
                resultIndexes[i] = -1;
            } else {
                fieldColumns.add(fieldColumn);
                resultIndexes[i] = index++;
            }
        }
        if (index == 1) {
            throw new CommonDaoException("All fields are excluded: {}", excludes.toExcludesString());
        }
        return findById(id, fieldColumns, resultIndexes, resultHandler);
    }

    public <T> T findByWhere(String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return null;
    }

    public <T> T findByWhere(
            Includes includes, String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return null;
    }

    public <T> T findByWhere(
            Excludes excludes, String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return null;
    }

    public <T> T findFieldById(String fieldName, Class<T> resultType, Object id) {
        return null;
    }

    public <T> T findFieldByWhere(String fieldName, Class<T> resultType, String whereClause, Object[] statementArgs) {
        return null;
    }

    protected <T> T findById(Object id, List<FieldColumn> fieldColumns,
                             int[] resultIndexes, ResultHandler<T> resultHandler) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        ResultSet         resultSet  = null;
        ClassTable        classTable = this.classTable;
        CharsWriter       cbuf       = new CharsWriter();
        cbuf.append("SELECT ").write(fieldColumns.get(0).getColumn().getColumnName());
        for (int i = 1, j = fieldColumns.size(); i < j; i++) {
            cbuf.append(',').write(fieldColumns.get(i).getColumn().getColumnName());
        }
        cbuf.append("FROM ").append(classTable.getTable().getTableName())
                .append(" WHERE ").append(classTable.getFieldColumn(0).getColumn().getColumnName()).write("=?");
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(cbuf.closeThen().toString());
            JdbcUtils.setStatementArg(statement, 1, id);
            resultSet = statement.executeQuery();
            return resultHandler.handleResult(new QueryContext(connection, statement, resultSet, resultIndexes));
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement, resultSet);
        }
    }

    protected int executeUpdate(String sqlPrefix, String sqlSuffix, Object... statementArgs) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(
                    new CharsWriter().append(sqlPrefix).append(sqlSuffix).closeThen().toString());
            JdbcUtils.setStatementArgs(statement, statementArgs);
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    protected static boolean contains(Set<String> set, FieldColumn fieldColumn) {
        return set.contains(fieldColumn.getColumn().getColumnName()) || set.contains(fieldColumn.getField().getName());
    }

}
