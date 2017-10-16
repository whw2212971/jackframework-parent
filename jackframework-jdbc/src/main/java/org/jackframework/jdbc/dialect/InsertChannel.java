package org.jackframework.jdbc.dialect;

import java.util.List;

public interface InsertChannel {

    void insert(Object dataObject);

    void insertList(List<?> dataList);

}
