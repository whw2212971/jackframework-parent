package org.jackframework.jdbc.parts;

public interface ResultHandler<T> {

    T handleResult(QueryContext queryContext);

}
