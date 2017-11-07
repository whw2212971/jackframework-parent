package org.jackframework.jdbc.jdbc;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.reflect.FastConstructor;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.orm.FieldColumn;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ResultHandlers {

    protected static final FieldResultHandler     FIELD_RESULT_HANDLER      = new FieldResultHandler();
    protected static final FieldListResultHandler FIELD_LIST_RESULT_HANDLER = new FieldListResultHandler();

    public static final MapResultHandler     MAP_RESULT_HANDLER      = new MapResultHandler();
    public static final MapListResultHandler MAP_LIST_RESULT_HANDLER = new MapListResultHandler();

    public static final BooleanResultHandler BOOLEAN_RESULT_HANDLER = new BooleanResultHandler();
    public static final IntegerResultHandler INTEGER_RESULT_HANDLER = new IntegerResultHandler();

    public static <T> ResultHandler<T> createUniqueResultHandler(Class<T> resultType) {
        return new UniqueResultHandler(getFastConstructor(resultType));
    }

    public static <T> ResultHandler<List<T>> createListResultHandler(Class<T> resultType) {
        return new ListResultHandler<T>(getFastConstructor(resultType));
    }

    public static <T> ResultHandler<T> getFieldHandler() {
        return FIELD_RESULT_HANDLER;
    }

    public static <T> ResultHandler<List<T>> getFieldListHandler() {
        return FIELD_LIST_RESULT_HANDLER;
    }

    public static class UniqueResultHandler<T> implements ResultHandler<T> {

        protected FastConstructor<T> constructor;

        public UniqueResultHandler(FastConstructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T handleResult(QueryContext<T> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            if (resultSet.next()) {
                T   dataObject = constructor.newInstance();
                int index      = 1;
                for (FieldColumn fieldColumn : queryContext.getSelectedColumns()) {
                    fieldColumn.setResultValue(resultSet, index++, dataObject);
                }
                return dataObject;
            }
            return null;
        }

    }

    public static class ListResultHandler<T> implements ResultHandler<List<T>> {

        protected FastConstructor<T> constructor;

        public ListResultHandler(FastConstructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public List<T> handleResult(QueryContext<List<T>> queryContext) throws SQLException {
            ResultSet         resultSet    = queryContext.getResultSet();
            List<FieldColumn> fieldColumns = queryContext.getSelectedColumns();
            List<T>           result       = new ArrayList<T>();
            while (resultSet.next()) {
                T   dataObject = constructor.newInstance();
                int index      = 1;
                for (FieldColumn fieldColumn : fieldColumns) {
                    fieldColumn.setResultValue(resultSet, index++, dataObject);
                }
                result.add(dataObject);
            }
            return result;
        }
    }

    public static class FieldResultHandler<T> implements ResultHandler<T> {

        @Override
        public T handleResult(QueryContext<T> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            if (resultSet.next()) {
                return (T) queryContext.getSelectedColumns().get(0).getResultValue(resultSet, 1);
            }
            return null;
        }

    }

    public static class FieldListResultHandler<T> implements ResultHandler<List<T>> {

        @Override
        public List<T> handleResult(QueryContext<List<T>> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            List<T>   result    = new ArrayList<T>();
            if (resultSet.next()) {
                result.add((T) queryContext.getSelectedColumns().get(0).getResultValue(resultSet, 1));
            }
            return result;
        }
    }

    public static class BooleanResultHandler implements ResultHandler<Boolean> {

        @Override
        public Boolean handleResult(QueryContext<Boolean> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
            return Boolean.FALSE;
        }

    }

    public static class IntegerResultHandler implements ResultHandler<Integer> {

        @Override
        public Integer handleResult(QueryContext<Integer> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }

    }

    public static class MapResultHandler implements ResultHandler<Map<String, Object>> {

        @Override
        public Map<String, Object> handleResult(QueryContext<Map<String, Object>> queryContext) throws SQLException {
            ResultSet resultSet = queryContext.getResultSet();
            if (resultSet.next()) {
                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                int                 index   = 1;
                for (FieldColumn fieldColumn : queryContext.getSelectedColumns()) {
                    dataMap.put(fieldColumn.getFieldName(), fieldColumn.getResultValue(resultSet, index++));
                }
                return dataMap;
            }
            return null;
        }

    }

    public static class MapListResultHandler implements ResultHandler<List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> handleResult(
                QueryContext<List<Map<String, Object>>> queryContext) throws SQLException {
            ResultSet                 resultSet    = queryContext.getResultSet();
            List<FieldColumn>         fieldColumns = queryContext.getSelectedColumns();
            List<Map<String, Object>> result       = new ArrayList<Map<String, Object>>();
            while (resultSet.next()) {
                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                int                 index   = 1;
                for (FieldColumn fieldColumn : fieldColumns) {
                    dataMap.put(fieldColumn.getFieldName(), fieldColumn.getResultValue(resultSet, index++));
                }
                result.add(dataMap);
            }
            return result;
        }

    }

    protected static <T> FastConstructor<T> getFastConstructor(Class<T> type) {
        Constructor<T> constructor = CaptainTools.tryGetConstructor(type);
        if (constructor == null) {
            throw new CommonDaoException("Could not found the default constructor: {}", type.getClass().getName());
        }
        return FastConstructor.getFastConstructor(constructor);
    }

}
