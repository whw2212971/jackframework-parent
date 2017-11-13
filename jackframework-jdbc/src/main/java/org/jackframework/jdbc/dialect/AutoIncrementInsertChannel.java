package org.jackframework.jdbc.dialect;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.orm.ClassTable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class AutoIncrementInsertChannel implements InsertChannel {

    protected DataSource dataSource;
    protected ClassTable classTable;
    protected int        batchUpdateLimit;
    protected String     insertSql;

    public AutoIncrementInsertChannel(
            CommonDaoConfig commonDaoConfig, ClassTable classTable, int batchUpdateLimit) {
        this.dataSource = commonDaoConfig.getDataSource();
        this.classTable = classTable;
        this.batchUpdateLimit = batchUpdateLimit;
        this.insertSql = buildInsertSql(classTable);
    }

    @Override
    public void insert(Object dataObject) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        ResultSet         resultSet  = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            prepareInsertStatement(statement, dataObject);
            statement.execute();
            resultSet = statement.getGeneratedKeys();
            resultSet.next();
            setPrimaryKey(dataObject, resultSet);
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement, resultSet);
        }
    }

    @Override
    public void insertList(List<?> dataObjectList) {
        int               limit      = batchUpdateLimit;
        int               length     = dataObjectList.size();
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            if (length <= limit) {
                executeBatch(statement, dataObjectList, 0, length);
            } else {
                prepareBatchInsertStatement(statement, dataObjectList, 0, limit);
                statement.executeBatch();
                int times  = length / limit;
                int offset = limit;
                for (int i = 1; i < times; i++) {
                    statement.clearBatch();
                    executeBatch(statement, dataObjectList, offset, offset += limit);
                }
                if (offset < length) {
                    statement.clearBatch();
                    executeBatch(statement, dataObjectList, offset, length);
                }
            }
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    protected void prepareInsertStatement(PreparedStatement statement, Object dataObject) throws SQLException {
        ClassTable classTable = this.classTable;
        for (int i = 1, j = classTable.getFieldColumnsCount(); i < j; i++) {
            classTable.getFieldColumn(i).setStatementValue(statement, i, dataObject);
        }
    }

    protected void prepareBatchInsertStatement(PreparedStatement statement,
                                               List<?> dataObjectList, int offset, int end) throws SQLException {
        while (offset < end) {
            prepareInsertStatement(statement, dataObjectList.get(offset++));
            statement.addBatch();
        }
    }

    protected void executeBatch(PreparedStatement statement,
                                List<?> dataObjectList, int offset, int end) throws SQLException {
        prepareBatchInsertStatement(statement, dataObjectList, offset, end);
        statement.executeBatch();
        ResultSet resultSet = statement.getGeneratedKeys();
        try {
            while (offset < end) {
                resultSet.next();
                setPrimaryKey(dataObjectList.get(offset++), resultSet);
            }
        } finally {
            JdbcUtils.closeQuietly(resultSet);
        }
    }

    protected void setPrimaryKey(Object dataObject, ResultSet resultSet) throws SQLException {
        classTable.getFieldColumn(0).setResultValue(resultSet, 1, dataObject);
    }

    protected String buildInsertSql(ClassTable classTable) {
        CharsWriter cbuf = new CharsWriter();
        cbuf.append("INSERT INTO ")
                .append(classTable.getTable().getTableName()).append('(')
                .write(classTable.getFieldColumn(1).getColumnName());
        int length = classTable.getFieldColumnsCount();
        for (int i = 2; i < length; i++) {
            cbuf.append(',').write(classTable.getFieldColumn(i).getColumnName());
        }
        cbuf.write(") VALUES(?");
        for (int i = 2; i < length; i++) {
            cbuf.write(",?");
        }
        return cbuf.append(')').closeToString();
    }

}
