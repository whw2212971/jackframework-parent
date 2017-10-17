package org.jackframework.jdbc.jdbc;

import org.jackframework.common.reflect.FastMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultGetter {

    Object getResultValue(ResultSet resultSet, int index) throws SQLException;

    void setResultValue(ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException;

}
