package org.jackframework.jdbc.parts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryContext {

    protected Connection connection;

    protected PreparedStatement statement;

    protected ResultSet resultSet;

    protected int[] resultIndexes;

    public QueryContext(Connection connection, PreparedStatement statement,
                        ResultSet resultSet, int[] resultIndexes) {
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
        this.resultIndexes = resultIndexes;
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public int[] getResultIndexes() {
        return resultIndexes;
    }

}
