package org.jackframework.jdbc.core;

import org.jackframework.common.CaptainTools;
import org.jackframework.jdbc.parts.DataAccessChannel;
import org.jackframework.jdbc.parts.DataAccessChannelFactory;
import org.jackframework.jdbc.parts.ResultHandler;
import org.jackframework.jdbc.parts.ResultHandlerFactory;

import java.util.*;

public class CommonDao {

    protected static final Set<String> NULL_STRING_SET = new HashSet<String>();

    protected DataAccessChannelFactory dataAccessChannelFactory;

    protected ResultHandlerFactory resultHandlerFactory;

    public <T> void insert(T dataObject) {
        if (dataObject == null) {
            return;
        }
        getDataAccessChannel(dataObject.getClass()).insert(dataObject);
    }

    public <T> void insert(T... dataObjects) {
        if (dataObjects == null || dataObjects.length == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataObjects(dataObjects).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(dataObjects.getClass()).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(dataObjects.getClass()).insertList(list);
        }
    }

    public <T extends List<?>> void insert(T dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataList(dataList).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(dataList.getClass()).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(dataList.getClass()).insertList(list);
        }
    }

    public int delete(Class<?> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return 0;
        }
        return getDataAccessChannel(dataType).deleteById(id);
    }

    public int delete(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return 0;
        }
        return getDataAccessChannel(dataType).deleteByWhere(whereClause, statementArgs);
    }

    public int update(Object dataObject) {
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass()).updateAll(dataObject);
    }

    public int update(Class<?> dataType, String updateClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(updateClause)) {
            return 0;
        }
        return getDataAccessChannel(dataType).updateByWhere(updateClause, statementArgs);
    }

    public int updateOptimized(Object dataObject) {
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass()).updateOptimized(dataObject, NULL_STRING_SET);
    }

    @SuppressWarnings({"UseBulkOperation", "ManualArrayToCollectionCopy"})
    public int updateOptimized(Object dataObject, String... forceUpdateFields) {
        if (dataObject == null) {
            return 0;
        }
        Set<String> forceUpdateSet = new HashSet<String>(forceUpdateFields.length);
        for (String fieldName : forceUpdateFields) {
            forceUpdateSet.add(fieldName);
        }
        return getDataAccessChannel(dataObject.getClass()).updateOptimized(dataObject, forceUpdateSet);
    }

    public <T> T findOne(Class<T> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, getUniqueHandler(dataType));
    }

    public <T> T findOne(Class<T> dataType, Object id, Includes includes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, includes, getUniqueHandler(dataType));
    }

    public <T> T findOne(Class<T> dataType, Object id, Excludes excludes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, excludes, getUniqueHandler(dataType));
    }

    public <T> T findOne(Class<T> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(whereClause, statementArgs, getUniqueHandler(dataType));
    }

    public <T> T findOne(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                includes, whereClause, statementArgs, getUniqueHandler(dataType));
    }

    public <T> T findOne(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                excludes, whereClause, statementArgs, getUniqueHandler(dataType));
    }

    public <T> T findField(Class<?> dataType, String fieldName, Class<T> resultType, Object id) {
        checkDataTypeParam(dataType);
        checkFieldNameParam(fieldName);
        checkResultTypeParam(resultType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findFieldById(fieldName, resultType, id);
    }

    public <T> T findField(
            Class<?> dataType, String fieldName, Class<T> resultType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        checkFieldNameParam(fieldName);
        checkResultTypeParam(resultType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findFieldByWhere(fieldName, resultType, whereClause, statementArgs);
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, getMapHandler());
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Includes includes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, includes, getMapHandler());
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Excludes excludes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, excludes, getMapHandler());
    }

    public Map<String, Object> findMap(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(whereClause, statementArgs, getMapHandler());
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(includes, whereClause, statementArgs, getMapHandler());
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(excludes, whereClause, statementArgs, getMapHandler());
    }

    public <T> List<T> findList(Class<T> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(whereClause, statementArgs, getListHandler(dataType));
    }

    public <T> List<T> findList(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                includes, whereClause, statementArgs, getListHandler(dataType));
    }

    public <T> List<T> findList(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                excludes, whereClause, statementArgs, getListHandler(dataType));
    }

    public List<Map<String, Object>> findMapList(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(whereClause, statementArgs, getMapListHandler());
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(includes, whereClause, statementArgs, getMapListHandler());
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(excludes, whereClause, statementArgs, getMapListHandler());
    }

    protected DataAccessChannel getDataAccessChannel(Class<?> dataType) {
        return dataAccessChannelFactory.getDataAccessChannel(dataType);
    }

    protected <T> ResultHandler<T> getUniqueHandler(Class<T> resultType) {
        return resultHandlerFactory.getUniqueHandler(resultType);
    }

    protected <T> ResultHandler<List<T>> getListHandler(Class<T> resultType) {
        return resultHandlerFactory.getListHandler(resultType);
    }

    protected ResultHandler<Map<String, Object>> getMapHandler() {
        return resultHandlerFactory.getMapHandler();
    }

    protected ResultHandler<List<Map<String, Object>>> getMapListHandler() {
        return resultHandlerFactory.getMapListHandler();
    }

    protected Map<Class<?>, List<Object>> groupDataObjects(Object[] dataObjects) {
        Map<Class<?>, List<Object>> dataMap = new HashMap<Class<?>, List<Object>>();
        for (int i = 0, j = dataObjects.length; i < j; i++) {
            Object       dataObject = dataObjects[i];
            Class<?>     dataType   = dataObject.getClass();
            List<Object> list       = dataMap.get(dataType);
            if (list == null) {
                dataMap.put(dataType, list = new ArrayList<Object>(j - i));
            }
            list.add(dataObject);
        }
        return dataMap;
    }

    protected Map<Class<?>, List<Object>> groupDataList(List<?> dataObjects) {
        Map<Class<?>, List<Object>> dataMap = new HashMap<Class<?>, List<Object>>();
        int                         size    = dataObjects.size();
        int                         index   = 0;
        for (Object dataObject : dataObjects) {
            Class<?>     dataType = dataObject.getClass();
            List<Object> list     = dataMap.get(dataType);
            if (list == null) {
                dataMap.put(dataType, list = new ArrayList<Object>(size - index));
            }
            list.add(dataObject);
            index++;
        }
        return dataMap;
    }

    protected static void checkDataTypeParam(Class<?> dataType) {
        if (dataType == null) {
            throw new CommonDaoException("Parameter 'dataType' is required.");
        }
    }

    protected static void checkFieldNameParam(String fieldName) {
        if (CaptainTools.isBlank(fieldName)) {
            throw new CommonDaoException("Parameter 'fieldName' is required.");
        }
    }

    protected static void checkResultTypeParam(Class<?> resultType) {
        if (CaptainTools.isBlank(resultType)) {
            throw new CommonDaoException("Parameter 'resultType' is required.");
        }
    }

}
