package org.jackframework.jdbc.dialect;

public interface ExistsChannel {

    boolean exists(Object id);

    boolean exists(String whereClause, Object[] statementArgs);


}
