package org.jackframework.jdbc.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementSetter {

    void setValue(PreparedStatement statement, int index, Object value) throws SQLException;

}
