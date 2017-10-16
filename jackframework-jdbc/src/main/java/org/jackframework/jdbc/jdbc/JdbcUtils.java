package org.jackframework.jdbc.jdbc;

import org.jackframework.common.CharsWriter;
import org.jackframework.common.exceptions.RunningException;
import org.jackframework.jdbc.core.CommonDaoException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;

public class JdbcUtils {

    public static void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            // Skip
        }
    }

    public static void closeQuietly(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            // Skip
        }
    }

    public static void closeQuietly(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            // Skip
        }
    }

    public static void closeQuietly(Connection Connection, Statement statement) {
        closeQuietly(statement);
        closeQuietly(Connection);
    }

    public static void closeQuietly(Connection Connection, Statement statement, ResultSet resultSet) {
        closeQuietly(resultSet);
        closeQuietly(statement);
        closeQuietly(Connection);
    }

    public static void setStatementArgs(PreparedStatement statement, Object... statementArgs)
            throws SQLException {
        if (statementArgs != null) {
            for (int i = 0, j = statementArgs.length; i < j; i++) {
                setStatementArg(statement, i + 1, statementArgs[i]);
            }
        }
    }

    public static void setStatementArg(PreparedStatement statement, int index, Object argument)
            throws SQLException {
        if (argument == null) {
            statement.setString(index, null);
            return;
        }
        Class<?> type = argument.getClass();
        if (type == String.class) {
            statement.setString(index, (String) argument);
        } else if (type == Integer.class) {
            statement.setInt(index, (Integer) argument);
        } else if (type == Long.class) {
            statement.setLong(index, (Long) argument);
        } else if (type == BigDecimal.class) {
            statement.setBigDecimal(index, (BigDecimal) argument);
        } else if (type == Boolean.class) {
            statement.setBoolean(index, (Boolean) argument);
        } else if (argument == java.util.Date.class) {
            statement.setTimestamp(index, new Timestamp(((java.util.Date) argument).getTime()));
        } else if (argument == Timestamp.class) {
            statement.setTimestamp(index, (Timestamp) argument);
        } else if (argument == Date.class) {
            statement.setDate(index, (Date) argument);
        } else if (argument == Time.class) {
            statement.setTime(index, (Time) argument);
        } else if (type == Double.class) {
            statement.setDouble(index, (Double) argument);
        } else if (type == Float.class) {
            statement.setFloat(index, (Float) argument);
        } else if (type == BigInteger.class) {
            statement.setBigDecimal(index, new BigDecimal((BigInteger) argument));
        } else if (type == Byte.class) {
            statement.setByte(index, (Byte) argument);
        } else if (type == Short.class) {
            statement.setShort(index, (Short) argument);
        } else if (type == Character.class) {
            statement.setInt(index, (Character) argument);
        } else {
            throw new CommonDaoException(
                    "Could not set the {} into the {}.", type.getName(), PreparedStatement.class.getName());
        }
    }

    public static String toParamList(int length) {
        CharsWriter cbuf = new CharsWriter();
        cbuf.write('?');
        for (int i = 1; i < length; i++) {
            cbuf.write(",?");
        }
        return cbuf.toString();
    }

    public static int getFirstIntResult(ResultSet resultSet) throws SQLException {
        try {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            throw new CommonDaoException("Empty result");
        } finally {
            closeQuietly(resultSet);
        }
    }

    public static long getFirstLongResult(ResultSet resultSet) throws SQLException {
        try {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            throw new CommonDaoException("Empty result");
        } finally {
            closeQuietly(resultSet);
        }
    }

}