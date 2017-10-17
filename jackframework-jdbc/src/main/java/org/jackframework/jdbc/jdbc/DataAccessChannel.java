package org.jackframework.jdbc.jdbc;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.core.Excludes;
import org.jackframework.jdbc.core.Includes;
import org.jackframework.jdbc.dialect.InsertChannel;
import org.jackframework.jdbc.orm.ClassTable;
import org.jackframework.jdbc.orm.FieldColumn;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class DataAccessChannel {

    protected DataSource    dataSource;
    protected InsertChannel insertChannel;
    protected ClassTable    classTable;

    protected String deleteByIdSql;
    protected String deleteByWherePrefix;

    protected String updateAllSql;
    protected String updateOptimizePrefix;
    protected String updateByWherePrefix;

    protected ResultHandler uniqueResultHandler;
    protected ResultHandler listResultHandler;

    public DataAccessChannel(CommonDaoConfig config, ClassTable classTable) {
        Class<?> dataType = classTable.getDataType();
        this.dataSource = config.getDataSource();
        this.insertChannel = config.getInsertChannelFactory().createInsertChannel(config, classTable);
        this.deleteByIdSql = buildDeleteByIdSql(classTable);
        this.deleteByWherePrefix = buildDeleteByWherePrefix(classTable);
        this.updateAllSql = buildUpdateAllSql(classTable);
        this.updateOptimizePrefix = buildUpdateOptimizePrefix(classTable);
        this.updateByWherePrefix = buildUpdateByWherePrefix(classTable);
        this.uniqueResultHandler = ResultHandlers.createUniqueResultHandler(dataType);
        this.listResultHandler = ResultHandlers.createListResultHandler(dataType);
    }

    public void insert(Object dataObject) {
        insertChannel.insert(dataObject);
    }

    public void insertList(List<?> dataList) {
        insertChannel.insertList(dataList);
    }

    public int deleteById(Object id) {
        return update(deleteByIdSql, Collections.singletonList(createStatementParam(id)));
    }

    public int deleteByWhere(String whereClause, Object[] statementArgs) {
        CharsWriter cbuf = new CharsWriter().append(deleteByWherePrefix);
        buildWhereSql(cbuf, whereClause);
        return update(cbuf.closeToString(), createStatementParams(statementArgs));
    }

    public int updateAll(Object dataObject) {
        ClassTable  classTable = this.classTable;
        FieldColumn idColumn   = classTable.getFieldColumn(0);
        Object      id         = idColumn.getValue(dataObject);
        if (id == null) {
            return 0;
        }

        int                  fieldCount = classTable.getFieldColumnsCount();
        List<StatementParam> params     = new ArrayList<StatementParam>(fieldCount);

        for (int i = 1; i < fieldCount; i++) {
            params.add(createStatementParam(classTable.getFieldColumn(i), dataObject));
        }
        params.add(createStatementParam(idColumn, dataObject));

        return update(updateAllSql, params);
    }

    public int updateByWhere(String updateClause, Object[] statementArgs) {
        return update(
                new CharsWriter().append(updateByWherePrefix).append(updateClause).closeToString(),
                createStatementParams(statementArgs));
    }

    public int updateOptimized(Object dataObject, Set<String> forceUpdateFields) {
        ClassTable  classTable = this.classTable;
        FieldColumn idColumn   = classTable.getFieldColumn(0);
        Object      id         = idColumn.getValue(dataObject);
        if (id == null) {
            return 0;
        }

        CharsWriter          cbuf       = new CharsWriter().append(updateOptimizePrefix);
        int                  fieldCount = classTable.getFieldColumnsCount();
        List<StatementParam> params     = new ArrayList<StatementParam>(fieldCount);
        boolean              isFirst    = true;

        for (int i = 0; i < fieldCount; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (fieldColumn.getValue(dataObject) != null || contains(forceUpdateFields, fieldColumn)) {
                if (isFirst) {
                    cbuf.write("set ");
                    isFirst = false;
                } else {
                    cbuf.write(',');
                }
                cbuf.append(fieldColumn.getColumnName()).append("=?");
                params.add(createStatementParam(classTable.getFieldColumn(i), dataObject));
            }
        }

        params.add(createStatementParam(idColumn, dataObject));

        return update(cbuf.closeToString(), params);
    }

    public <T> T findById(Object id, ResultHandler<T> resultHandler) {
        return findById(id, classTable.getFieldColumns(), resultHandler);
    }

    public <T> T findById(Object id, Includes includes, ResultHandler<T> resultHandler) {
        return findById(id, buildSelectedColumns(includes), resultHandler);
    }

    public <T> T findById(Object id, Excludes excludes, ResultHandler<T> resultHandler) {
        return findById(id, buildSelectedColumns(excludes), resultHandler);
    }

    public <T> T findByWhere(String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return findByWhere(whereClause, statementArgs, classTable.getFieldColumns(), resultHandler);
    }

    public <T> T findByWhere(
            Includes includes, String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return findByWhere(whereClause, statementArgs, buildSelectedColumns(includes), resultHandler);
    }

    public <T> T findByWhere(
            Excludes excludes, String whereClause, Object[] statementArgs, ResultHandler<T> resultHandler) {
        return findByWhere(whereClause, statementArgs, buildSelectedColumns(excludes), resultHandler);
    }

    public ResultHandler getUniqueResultHandler() {
        return uniqueResultHandler;
    }

    public ResultHandler getListResultHandler() {
        return listResultHandler;
    }

    protected int update(String sql, List<StatementParam> params) {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setSql(sql);
        updateContext.setStatementParams(params);
        return executeUpdate(updateContext);
    }

    protected <T> T findById(Object id, List<FieldColumn> selectedColumns, ResultHandler<T> resultHandler) {
        CharsWriter     cbuf         = new CharsWriter();
        QueryContext<T> queryContext = new QueryContext<T>();

        queryContext.setSelectedColumns(selectedColumns);

        buildSelectClause(cbuf, queryContext);
        cbuf.append(" WHERE ").append(classTable.getFieldColumn(0).getColumnName()).write("=?");

        queryContext.setSql(cbuf.closeToString());
        queryContext.setStatementParams(Collections.singletonList(
                new StatementParam(id, JdbcUtils.getStatementSetter(id))));
        queryContext.setResultHandler(resultHandler);

        return executeQuery(queryContext);
    }

    protected <T> T findByWhere(String whereClause, Object[] statementArgs,
                                List<FieldColumn> selectedColumns, ResultHandler<T> resultHandler) {
        CharsWriter     cbuf         = new CharsWriter();
        QueryContext<T> queryContext = new QueryContext<T>();

        queryContext.setSelectedColumns(selectedColumns);
        buildSelectClause(cbuf, queryContext);
        buildWhereSql(cbuf, whereClause);

        queryContext.setSql(cbuf.closeToString());
        queryContext.setStatementParams(createStatementParams(statementArgs));
        queryContext.setResultHandler(resultHandler);

        return executeQuery(queryContext);
    }

    protected List<FieldColumn> buildSelectedColumns(Includes includes) {
        ClassTable        classTable      = this.classTable;
        int               length          = classTable.getFieldColumnsCount();
        List<FieldColumn> selectedColumns = new ArrayList<FieldColumn>(length);
        for (int i = 0; i < length; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (includes.contains(fieldColumn)) {
                selectedColumns.add(fieldColumn);
            }
        }
        if (selectedColumns.size() == 0) {
            throw new CommonDaoException("Could not found any include fields: {}", includes.toIncludesString());
        }
        return selectedColumns;
    }

    protected List<FieldColumn> buildSelectedColumns(Excludes excludes) {
        ClassTable        classTable      = this.classTable;
        int               length          = classTable.getFieldColumnsCount();
        List<FieldColumn> selectedColumns = new ArrayList<FieldColumn>(length);
        for (int i = 0; i < length; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (excludes.contains(fieldColumn)) {
                continue;
            }
            selectedColumns.add(fieldColumn);
        }
        if (selectedColumns.size() == 0) {
            throw new CommonDaoException("All fields are excluded: {}", excludes.toExcludesString());
        }
        return selectedColumns;
    }

    protected void buildSelectClause(CharsWriter cbuf, QueryContext<?> queryContext) {
        List<FieldColumn> selectedColumns = queryContext.getSelectedColumns();
        cbuf.append("SELECT ").write(selectedColumns.get(0).getColumnName());
        for (int i = 1, j = selectedColumns.size(); i < j; i++) {
            cbuf.append(',').write(selectedColumns.get(i).getColumnName());
        }
        cbuf.append(" FROM ").write(classTable.getTable().getTableName());
    }

    protected int executeUpdate(UpdateContext updateContext) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            // Create database connection
            connection = dataSource.getConnection();

            // Prepare statement parameters
            statement = connection.prepareStatement(updateContext.getSql());
            JdbcUtils.prepareStatement(statement, updateContext.getStatementParams());

            // Execute update
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            // Release the database resources
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    protected <T> T executeQuery(QueryContext<T> queryContext) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        ResultSet         resultSet  = null;
        DataSource        dataSource = this.dataSource;
        try {
            // Create database connection
            connection = dataSource.getConnection();

            // Prepare statement parameters
            statement = connection.prepareStatement(queryContext.getSql());
            JdbcUtils.prepareStatement(statement, queryContext.getStatementParams());

            // Execute query
            resultSet = statement.executeQuery();

            // Set query context
            queryContext.setDataSource(dataSource);
            queryContext.setConnection(connection);
            queryContext.setStatement(statement);
            queryContext.setResultSet(resultSet);

            // Handle result
            return queryContext.getResultHandler().handleResult(queryContext);
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            // Release the database resources
            JdbcUtils.closeQuietly(connection, statement, resultSet);
        }
    }

    protected static StatementParam createStatementParam(FieldColumn fieldColumn, Object dataObject) {
        Object value = fieldColumn.getValue(dataObject);
        if (value == null) {
            return new StatementParam(null, StatementSetters.NULL_SETTER);
        }
        return new StatementParam(fieldColumn.getValue(dataObject), fieldColumn.getStatementSetter());
    }

    protected static StatementParam createStatementParam(Object argument) {
        return new StatementParam(argument, JdbcUtils.getStatementSetter(argument));
    }

    protected static List<StatementParam> createStatementParams(Object[] statementArgs) {
        List<StatementParam> params;
        if (statementArgs == null || statementArgs.length == 0) {
            params = Collections.emptyList();
        } else {
            int length = statementArgs.length;
            params = new ArrayList<StatementParam>(length);
            for (Object param : statementArgs) {
                params.add(new StatementParam(param, JdbcUtils.getStatementSetter(param)));
            }
        }
        return params;
    }

    protected static boolean contains(Set<String> set, FieldColumn fieldColumn) {
        return set.contains(fieldColumn.getColumnName()) || set.contains(fieldColumn.getFieldName());
    }

    protected static void buildWhereSql(CharsWriter cbuf, String whereClause) {
        if (!startWithWhere(whereClause)) {
            cbuf.append(" WHERE ");
        } else if (whereClause.charAt(0) > ' ') {
            cbuf.append(' ');
        }
        cbuf.write(whereClause);
    }

    protected static boolean startWithWhere(String whereClause) {
        int index  = 0;
        int length = whereClause.length();
        while (index < length) {
            if (whereClause.charAt(index) <= ' ') {
                index++;
                continue;
            }
            break;
        }
        return index < length && Character.toUpperCase(whereClause.charAt(index++)) == 'W' &&
                index < length && Character.toUpperCase(whereClause.charAt(index++)) == 'H' &&
                index < length && Character.toUpperCase(whereClause.charAt(index++)) == 'E' &&
                index < length && Character.toUpperCase(whereClause.charAt(index++)) == 'R' &&
                index < length && Character.toUpperCase(whereClause.charAt(index)) == 'E';
    }

    protected static String buildDeleteByIdSql(ClassTable classTable) {
        return new CharsWriter().append("DELETE FROM ")
                .append(classTable.getTable().getTableName())
                .append(" WHERE ").append(classTable.getFieldColumn(0).getColumnName())
                .append("=?").closeToString();
    }

    protected static String buildDeleteByWherePrefix(ClassTable classTable) {
        return new CharsWriter().append("DELETE FROM ")
                .append(classTable.getTable().getTableName()).append(' ').closeToString();
    }

    protected static String buildUpdateAllSql(ClassTable classTable) {
        CharsWriter cbuf = new CharsWriter();
        cbuf.append("UPDATE ").write(classTable.getTable().getTableName());
        int length = classTable.getFieldColumnsCount();
        if (1 < length) {
            cbuf.append(" SET ").append(classTable.getFieldColumn(1).getColumnName()).write("=?");
            for (int i = 2; i < length; i++) {
                cbuf.append(',').append(classTable.getFieldColumn(i).getColumnName()).write("=?");
            }
        }
        return cbuf.append(" WHERE ")
                .append(classTable.getTable().getColumn(0).getColumnName()).append("=?").closeToString();
    }

    protected static String buildUpdateByWherePrefix(ClassTable classTable) {
        return new CharsWriter().append("UPDATE ")
                .append(classTable.getTable().getTableName()).append(" ").closeToString();
    }

    protected static String buildUpdateOptimizePrefix(ClassTable classTable) {
        String idNameName = classTable.getFieldColumn(0).getColumnName();
        return new CharsWriter().append("UPDATE ").append(classTable.getTable().getTableName())
                .append(" SET ").append(idNameName).append('=').append(idNameName).closeToString();
    }

}
