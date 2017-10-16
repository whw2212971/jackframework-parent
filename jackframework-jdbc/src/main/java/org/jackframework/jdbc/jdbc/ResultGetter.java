package org.jackframework.jdbc.jdbc;

import java.sql.ResultSet;

public interface ResultGetter {

    Object getResultValue(ResultSet resultSet, int index);

}
