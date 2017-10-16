package org.jackframework.jdbc.parts;

import java.util.List;
import java.util.Map;

public class ResultHandlerFactory {

    public <T> ResultHandler<T> getUniqueHandler(Class<T> resultType) {
        return null;
    }

    public <T> ResultHandler<List<T>> getListHandler(Class<T> resultType) {
        return null;
    }

    public ResultHandler<Map<String, Object>> getMapHandler() {
        return null;
    }

    public ResultHandler<List<Map<String, Object>>> getMapListHandler() {
        return null;
    }

}
