package org.jackframework.jdbc.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementParam {

    protected Object statementArg;

    protected StatementSetter statementSetter;

    public StatementParam(Object statementArg, StatementSetter statementSetter) {
        this.statementArg = statementArg;
        this.statementSetter = statementSetter;
    }

    public void prepareStatement(PreparedStatement statement, int index) throws SQLException {
        statementSetter.setValue(statement, index, statementArg);
    }

    public Object getStatementArg() {
        return statementArg;
    }

    public void setStatementArg(Object statementArg) {
        this.statementArg = statementArg;
    }

    public StatementSetter getStatementSetter() {
        return statementSetter;
    }

    public void setStatementSetter(StatementSetter statementSetter) {
        this.statementSetter = statementSetter;
    }

}
