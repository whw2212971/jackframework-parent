package org.jackframework.jdbc.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public abstract class JdbcContext {

    protected String sql;

    protected DataSource dataSource;

    protected Connection connection;

    protected PreparedStatement statement;

    protected List<StatementParam> statementParams;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public void setStatement(PreparedStatement statement) {
        this.statement = statement;
    }

    public List<StatementParam> getStatementParams() {
        return statementParams;
    }

    public void setStatementParams(List<StatementParam> statementParams) {
        this.statementParams = statementParams;
    }

}
