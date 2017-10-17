package org.jackframework.jdbc.jdbc;

import java.sql.SQLException;

public interface ResultHandler<T> {

    T handleResult(QueryContext<T> queryContext) throws SQLException;

}
