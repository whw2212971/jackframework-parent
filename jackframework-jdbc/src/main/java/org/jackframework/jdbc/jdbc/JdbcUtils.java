package org.jackframework.jdbc.jdbc;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoException;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class JdbcUtils {

    protected static final Map<Type, StatementSetter> STATEMENT_SETTER_MAP = new HashMap<Type, StatementSetter>();
    protected static final Map<Type, ResultGetter>    RESULT_GETTER_MAP    = new HashMap<Type, ResultGetter>();

    static {
        STATEMENT_SETTER_MAP.put(String.class, StatementSetters.STRING_SETTER);

        STATEMENT_SETTER_MAP.put(char.class, StatementSetters.STRING_SETTER);
        STATEMENT_SETTER_MAP.put(Character.class, StatementSetters.STRING_SETTER);

        STATEMENT_SETTER_MAP.put(int.class, StatementSetters.INTEGER_SETTER);
        STATEMENT_SETTER_MAP.put(Integer.class, StatementSetters.INTEGER_SETTER);

        STATEMENT_SETTER_MAP.put(long.class, StatementSetters.LONG_SETTER);
        STATEMENT_SETTER_MAP.put(Long.class, StatementSetters.LONG_SETTER);

        STATEMENT_SETTER_MAP.put(boolean.class, StatementSetters.BOOLEAN_SETTER);
        STATEMENT_SETTER_MAP.put(Boolean.class, StatementSetters.BOOLEAN_SETTER);

        STATEMENT_SETTER_MAP.put(java.util.Date.class, StatementSetters.J_UTIL_DATE_SETTER);
        STATEMENT_SETTER_MAP.put(java.sql.Date.class, StatementSetters.J_SQL_DATE_SETTER);
        STATEMENT_SETTER_MAP.put(Timestamp.class, StatementSetters.J_SQL_TIMESTAMP_SETTER);
        STATEMENT_SETTER_MAP.put(Time.class, StatementSetters.J_SQL_TIME_SETTER);

        STATEMENT_SETTER_MAP.put(BigDecimal.class, StatementSetters.BIG_DECIMAL_SETTER);
        STATEMENT_SETTER_MAP.put(BigInteger.class, StatementSetters.BIG_INTEGER_SETTER);

        STATEMENT_SETTER_MAP.put(double.class, StatementSetters.DOUBLE_SETTER);
        STATEMENT_SETTER_MAP.put(Double.class, StatementSetters.DOUBLE_SETTER);

        STATEMENT_SETTER_MAP.put(float.class, StatementSetters.FLOAT_SETTER);
        STATEMENT_SETTER_MAP.put(Float.class, StatementSetters.FLOAT_SETTER);

        STATEMENT_SETTER_MAP.put(byte.class, StatementSetters.BYTE_SETTER);
        STATEMENT_SETTER_MAP.put(Byte.class, StatementSetters.BYTE_SETTER);

        STATEMENT_SETTER_MAP.put(short.class, StatementSetters.SHORT_SETTER);
        STATEMENT_SETTER_MAP.put(Short.class, StatementSetters.SHORT_SETTER);

        RESULT_GETTER_MAP.put(String.class, ResultGetters.STRING_RESULT_GETTER);

        RESULT_GETTER_MAP.put(int.class, ResultGetters.INTEGER_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Integer.class, ResultGetters.INTEGER_RESULT_GETTER);

        RESULT_GETTER_MAP.put(long.class, ResultGetters.LONG_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Long.class, ResultGetters.LONG_RESULT_GETTER);

        RESULT_GETTER_MAP.put(boolean.class, ResultGetters.BOOLEAN_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Boolean.class, ResultGetters.BOOLEAN_RESULT_GETTER);

        RESULT_GETTER_MAP.put(java.util.Date.class, ResultGetters.J_UTIL_DATE_RESULT_GETTER);
        RESULT_GETTER_MAP.put(java.sql.Date.class, ResultGetters.J_SQL_DATE_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Timestamp.class, ResultGetters.J_SQL_TIMESTAMP_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Time.class, ResultGetters.J_SQL_TIME_RESULT_GETTER);

        RESULT_GETTER_MAP.put(BigDecimal.class, ResultGetters.BIG_DECIMAL_RESULT_GETTER);
        RESULT_GETTER_MAP.put(BigInteger.class, ResultGetters.BIG_INTEGER_RESULT_GETTER);

        RESULT_GETTER_MAP.put(double.class, ResultGetters.DOUBLE_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Double.class, ResultGetters.DOUBLE_RESULT_GETTER);

        RESULT_GETTER_MAP.put(float.class, ResultGetters.FLOAT_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Float.class, ResultGetters.BYTE_RESULT_GETTER);

        RESULT_GETTER_MAP.put(short.class, ResultGetters.SHORT_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Short.class, ResultGetters.SHORT_RESULT_GETTER);

        RESULT_GETTER_MAP.put(char.class, ResultGetters.CHARACTER_RESULT_GETTER);
        RESULT_GETTER_MAP.put(Character.class, ResultGetters.CHARACTER_RESULT_GETTER);
    }

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

    public static StatementSetter getStatementSetter(Object argument) {
        if (argument == null) {
            return StatementSetters.NULL_SETTER;
        }
        return getStatementSetter(argument.getClass());
    }

    public static StatementSetter getStatementSetter(Class<?> type) {
        StatementSetter statementSetter = STATEMENT_SETTER_MAP.get(type);
        if (statementSetter == null) {
            throw new CommonDaoException("Could not set {} parameter into the jdbc statement.", type.getName());
        }
        return statementSetter;
    }

    protected static void prepareStatement(
            PreparedStatement statement, List<StatementParam> params) throws SQLException {
        int index = 1;
        for (StatementParam param : params) {
            param.prepareStatement(statement, index++);
        }
    }

    public static ResultGetter getResultGetter(Class<?> type) {
        ResultGetter resultGetter = RESULT_GETTER_MAP.get(type);
        if (resultGetter == null) {
            throw new CommonDaoException("Could not get {} result from the jdbc result set.", type.getName());
        }
        return resultGetter;
    }

    public static <T> T executeQuery(QueryContext<T> queryContext) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        ResultSet         resultSet  = null;
        DataSource        dataSource = queryContext.getDataSource();
        try {
            // Create database connection
            connection = dataSource.getConnection();

            // Prepare statement parameters
            statement = connection.prepareStatement(queryContext.getSql());
            JdbcUtils.prepareStatement(statement, queryContext.getStatementParams());

            // Execute query
            resultSet = statement.executeQuery();

            queryContext.setConnection(connection);
            queryContext.setStatement(statement);
            queryContext.setResultSet(resultSet);

            // Handle result
            return queryContext.getResultHandler().handleResult(queryContext);
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            // Release the database resources
            closeQuietly(connection, statement, resultSet);
        }
    }

    public static int executeUpdate(UpdateContext updateContext) {
        Connection        connection = null;
        PreparedStatement statement  = null;
        try {
            // Create database connection
            connection = updateContext.getDataSource().getConnection();

            // Prepare statement parameters
            statement = connection.prepareStatement(updateContext.getSql());
            JdbcUtils.prepareStatement(statement, updateContext.getStatementParams());

            // Execute update
            return statement.executeUpdate();
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            // Release the database resources
            closeQuietly(connection, statement);
        }
    }

    public static StatementParam createStatementParam(Object argument) {
        return new StatementParam(argument, JdbcUtils.getStatementSetter(argument));
    }

    public static List<StatementParam> createStatementParams(Object[] statementArgs) {
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

    public static void buildWhereSql(CharsWriter cbuf, String whereClause) {
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

}