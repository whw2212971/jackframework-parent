package org.jackframework.jdbc.dialect;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.jdbc.QueryContext;
import org.jackframework.jdbc.jdbc.ResultHandlers;
import org.jackframework.jdbc.jdbc.StatementParam;
import org.jackframework.jdbc.orm.ClassTable;

import javax.sql.DataSource;
import java.util.Collections;

public class NormalExistsChannel implements ExistsChannel {

    protected DataSource dataSource;

    protected ClassTable classTable;

    protected String selectIdExistsSql;

    public NormalExistsChannel(DataSource dataSource, ClassTable classTable) {
        this.classTable = classTable;
        this.dataSource = dataSource;
        this.selectIdExistsSql = buildSelectIdExistsSql(classTable);
    }

    @Override
    public boolean exists(Object id) {
        QueryContext<Boolean> queryContext = new QueryContext<Boolean>();

        queryContext.setSql(selectIdExistsSql);
        queryContext.setStatementParams(
                Collections.singletonList(new StatementParam(id, JdbcUtils.getStatementSetter(id))));
        queryContext.setResultHandler(ResultHandlers.BOOLEAN_RESULT_HANDLER);

        // Set query context
        queryContext.setDataSource(dataSource);

        return JdbcUtils.executeQuery(queryContext);
    }

    @Override
    public boolean exists(String whereClause, Object[] statementArgs) {
        QueryContext<Boolean> queryContext = new QueryContext<Boolean>();

        queryContext.setSql(buildSelectExistsByWhereSql(classTable, whereClause));
        queryContext.setStatementParams(JdbcUtils.createStatementParams(statementArgs));
        queryContext.setResultHandler(ResultHandlers.BOOLEAN_RESULT_HANDLER);

        // Set query context
        queryContext.setDataSource(dataSource);

        return JdbcUtils.executeQuery(queryContext);
    }

    protected String buildSelectIdExistsSql(ClassTable classTable) {
        return new CharsWriter()
                .append("SELECT EXISTS(SELECT 1 FROM ")
                .append(classTable.getTable())
                .append("WHERE ")
                .append(classTable.getPrimaryFieldColumn().getColumnName())
                .append("=?) result")
                .closeToString();
    }

    protected String buildSelectExistsByWhereSql(ClassTable classTable, String whereClause) {
        CharsWriter cbuf = new CharsWriter()
                .append("SELECT EXISTS(SELECT 1 FROM ")
                .append(classTable.getTable());

        JdbcUtils.buildWhereSql(cbuf, whereClause);

        return cbuf.append(") result").closeToString();
    }

}
