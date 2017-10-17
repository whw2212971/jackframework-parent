package org.jackframework.jdbc.jdbc;

import org.jackframework.jdbc.orm.FieldColumn;

import java.sql.ResultSet;
import java.util.List;

public class QueryContext<T> extends ExecuteContext {

    protected ResultSet resultSet;

    protected List<FieldColumn> selectedColumns;

    protected ResultHandler<T> resultHandler;

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public List<FieldColumn> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<FieldColumn> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public ResultHandler<T> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
    }

}
