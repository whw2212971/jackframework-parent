package org.jackframework.jdbc.dialect;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.orm.ClassTable;
import org.jackframework.jdbc.orm.FieldColumn;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class AutoIncrementInsertChannel implements InsertChannel {

    protected DataSource dataSource;
    protected ClassTable classTable;
    protected int        batchUpdateLimit;

    public AutoIncrementInsertChannel(
            CommonDaoConfig commonDaoConfig, ClassTable classTable, int batchUpdateLimit) {
        this.dataSource = commonDaoConfig.getDataSource();
        this.classTable = classTable;
        this.batchUpdateLimit = batchUpdateLimit;
    }

    @Override
    public void insert(Object dataObject) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        ResultSet         resultSet  = null;
        String            sql        = buildInsertSql(dataObject, classTable);
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
            statement = connection.prepareStatement(buildInsertSql(dataObjectList, classTable));
            if (length <= limit) {
                prepareBatchInsertStatement(statement, dataObjectList, 0, length);
                statement.executeBatch();
            } else {
                prepareBatchInsertStatement(statement, dataObjectList, 0, limit);
                statement.executeBatch();
                int times  = length / limit;
                int offset = limit;
                for (int i = 1; i < times; i++) {
                    statement.clearBatch();
                    prepareBatchInsertStatement(statement, dataObjectList, offset, offset += limit);
                    statement.executeBatch();
                }
                if (offset < length) {
                    statement.clearBatch();
                    prepareBatchInsertStatement(statement, dataObjectList, offset, length);
                    statement.executeBatch();
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
            prepareInsertStatement(statement, dataObjectList);
            statement.addBatch();
        }
    }

    protected void setPrimaryKey(Object dataObject, ResultSet resultSet) throws SQLException {
        classTable.getFieldColumn(0).setResultValue(resultSet, 1, dataObject);
    }

    protected String buildInsertSql(Object dataObject, ClassTable classTable) {
        CharsWriter cbuf = new CharsWriter();
        cbuf.append("INSERT INTO ")
                .append(classTable.getTable().getTableName()).append('(');
        int     length  = classTable.getFieldColumnsCount();
        boolean isFirst = true;
        for (int i = 1; i < length; i++) {
            FieldColumn fieldColumn = classTable.getFieldColumn(i);
            if (fieldColumn.getValue(dataObject) == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
                cbuf.write(fieldColumn.getColumnName());
                continue;
            }
            cbuf.append(',').write(fieldColumn.getColumnName());
        }
        cbuf.write(") VALUES(");
        for (int i = 1; i < length; i++) {
            if (classTable.getFieldColumn(i).getValue(dataObject) == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
                cbuf.write("?");
                continue;
            }
            cbuf.write(",?");
        }
        return cbuf.append(')').closeToString();
    }

}
