package org.jackframework.jdbc.core;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.jdbc.DataAccessChannel;
import org.jackframework.jdbc.jdbc.DataAccessChannelFactory;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.jdbc.ResultHandlers;
import org.jackframework.jdbc.orm.ClassTable;
import org.jackframework.jdbc.orm.Table;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("unchecked")
public class CommonDao {

    protected static final Set<String> NULL_STRING_SET = new HashSet<String>();

    protected CommonDaoConfig          commonDaoConfig;
    protected DataAccessChannelFactory dataAccessChannelFactory;

    public <T> void insert(T dataObject) {
        if (dataObject == null) {
            return;
        }
        getDataAccessChannel(dataObject.getClass()).insert(dataObject);
    }

    public <T> void insert(String tableName, T dataObject) {
        checkTableNameParam(tableName);
        if (dataObject == null) {
            return;
        }
        getDataAccessChannel(dataObject.getClass(), tableName).insert(dataObject);
    }

    public <T> void insert(T... dataObjects) {
        if (dataObjects == null || dataObjects.length == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataObjects(dataObjects).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(entry.getKey()).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(entry.getKey()).insertList(list);
        }
    }

    public <T> void insert(String tableName, T... dataObjects) {
        checkTableNameParam(tableName);
        if (dataObjects == null || dataObjects.length == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataObjects(dataObjects).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(entry.getKey(), tableName).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(entry.getKey()).insertList(list);
        }
    }

    public <T extends List<?>> void insert(T dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataList(dataList).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(entry.getKey()).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(entry.getKey()).insertList(list);
        }
    }

    public <T extends List<?>> void insert(String tableName, T dataList) {
        checkTableNameParam(tableName);
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        for (Map.Entry<Class<?>, List<Object>> entry : groupDataList(dataList).entrySet()) {
            List<Object> list = entry.getValue();
            if (list.size() == 1) {
                getDataAccessChannel(entry.getKey(), tableName).insert(list.get(0));
                continue;
            }
            getDataAccessChannel(entry.getKey()).insertList(list);
        }
    }

    public int delete(Class<?> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return 0;
        }
        return getDataAccessChannel(dataType).deleteById(id);
    }

    public int delete(String tableName, Object id) {
        checkTableNameParam(tableName);
        if (id == null) {
            return 0;
        }
        return DataAccessChannel.update(getTable(tableName).getDeleteByIdSql(),
                                        Collections.singletonList(JdbcUtils.createStatementParam(id)),
                                        commonDaoConfig.getDataSource());
    }

    public int delete(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return 0;
        }
        return getDataAccessChannel(dataType).deleteByWhere(whereClause, statementArgs);
    }

    public int delete(String tableName, String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        if (CaptainTools.isBlank(whereClause)) {
            return 0;
        }
        CharsWriter cbuf = new CharsWriter().append(getTable(tableName).getDeleteByWherePrefix());
        JdbcUtils.buildWhereSql(cbuf, whereClause);
        return DataAccessChannel.update(cbuf.closeToString(),
                                        JdbcUtils.createStatementParams(statementArgs),
                                        commonDaoConfig.getDataSource());
    }

    public int update(Object dataObject) {
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass()).updateAll(dataObject);
    }

    public int update(String tableName, Object dataObject) {
        checkTableNameParam(tableName);
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass(), tableName).updateAll(dataObject);
    }

    public int update(Class<?> dataType, String updateClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(updateClause)) {
            return 0;
        }
        return getDataAccessChannel(dataType).updateByWhere(updateClause, statementArgs);
    }

    public int update(String tableName, String updateClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        if (CaptainTools.isBlank(updateClause)) {
            return 0;
        }
        return DataAccessChannel.update(new CharsWriter()
                                                .append(getTable(tableName).getUpdateByWherePrefix())
                                                .append(updateClause)
                                                .closeToString(),
                                        JdbcUtils.createStatementParams(statementArgs),
                                        commonDaoConfig.getDataSource());
    }

    public int updateOptimized(Object dataObject) {
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass()).updateOptimized(dataObject, NULL_STRING_SET);
    }

    public int updateOptimized(String tableName, Object dataObject) {
        checkTableNameParam(tableName);
        if (dataObject == null) {
            return 0;
        }
        return getDataAccessChannel(dataObject.getClass(), tableName).updateOptimized(dataObject, NULL_STRING_SET);
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

    @SuppressWarnings({"UseBulkOperation", "ManualArrayToCollectionCopy"})
    public int updateOptimized(String tableName, Object dataObject, String... forceUpdateFields) {
        checkTableNameParam(tableName);
        if (dataObject == null) {
            return 0;
        }
        Set<String> forceUpdateSet = new HashSet<String>(forceUpdateFields.length);
        for (String fieldName : forceUpdateFields) {
            forceUpdateSet.add(fieldName);
        }
        return getDataAccessChannel(dataObject.getClass(), tableName).updateOptimized(dataObject, forceUpdateSet);
    }

    public <T> T findOne(Class<T> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findById(id, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, Object id) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findById(id, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(Class<T> dataType, Object id, Includes includes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findById(id, includes, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, Object id, Includes includes) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findById(id, includes, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(Class<T> dataType, Object id, Excludes excludes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findById(id, excludes, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, Object id, Excludes excludes) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findById(id, excludes, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(Class<T> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findByWhere(
                whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findByWhere(
                whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findByWhere(
                includes, whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, Includes includes,
                         String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findByWhere(
                includes, whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (T) dataAccessChannel.findByWhere(
                excludes, whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findOne(String tableName, Class<T> dataType, Excludes excludes,
                         String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (T) dataAccessChannel.findByWhere(
                excludes, whereClause, statementArgs, dataAccessChannel.getUniqueResultHandler());
    }

    public <T> T findField(Class<?> dataType, String fieldName, Object id) {
        checkDataTypeParam(dataType);
        checkFieldNameParam(fieldName);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findById(id, Includes.include(fieldName), ResultHandlers.<T>getFieldHandler());
    }

    public <T> T findField(
            Class<?> dataType, String fieldName, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        checkFieldNameParam(fieldName);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                Includes.include(fieldName), whereClause, statementArgs, ResultHandlers.<T>getFieldHandler());
    }

    public <T> List<T> findFieldList(
            Class<?> dataType, String fieldName, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        checkFieldNameParam(fieldName);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType).findByWhere(
                Includes.include(fieldName), whereClause, statementArgs, ResultHandlers.<T>getFieldListHandler());
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Includes includes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, includes, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Excludes excludes) {
        checkDataTypeParam(dataType);
        if (id == null) {
            return null;
        }
        return getDataAccessChannel(dataType).findById(id, excludes, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public Map<String, Object> findMap(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(whereClause, statementArgs, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(includes, whereClause, statementArgs, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(excludes, whereClause, statementArgs, ResultHandlers.MAP_RESULT_HANDLER);
    }

    public <T> List<T> findList(Class<T> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (List<T>) dataAccessChannel.findByWhere(
                whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public <T> List<T> findList(String tableName, Class<T> dataType, String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (List<T>) dataAccessChannel.findByWhere(
                whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public <T> List<T> findList(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (List<T>) dataAccessChannel.findByWhere(
                includes, whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public <T> List<T> findList(String tableName, Class<T> dataType, Includes includes,
                                String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (List<T>) dataAccessChannel.findByWhere(
                includes, whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public <T> List<T> findList(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType);
        return (List<T>) dataAccessChannel.findByWhere(
                excludes, whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public <T> List<T> findList(String tableName, Class<T> dataType, Excludes excludes,
                                String whereClause, Object... statementArgs) {
        checkTableNameParam(tableName);
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        DataAccessChannel dataAccessChannel = getDataAccessChannel(dataType, tableName);
        return (List<T>) dataAccessChannel.findByWhere(
                excludes, whereClause, statementArgs, dataAccessChannel.getListResultHandler());
    }

    public List<Map<String, Object>> findMapList(Class<?> dataType, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(whereClause, statementArgs, ResultHandlers.MAP_LIST_RESULT_HANDLER);
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(includes, whereClause, statementArgs, ResultHandlers.MAP_LIST_RESULT_HANDLER);
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        checkDataTypeParam(dataType);
        if (CaptainTools.isBlank(whereClause)) {
            return null;
        }
        return getDataAccessChannel(dataType)
                .findByWhere(excludes, whereClause, statementArgs, ResultHandlers.MAP_LIST_RESULT_HANDLER);
    }

    public boolean exists(Class<?> dataType, Object id) {
        return getDataAccessChannel(dataType).exists(id);
    }

    public boolean exists(Class<?> dataType, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).exists(whereClause, statementArgs);
    }

    public int count(Class<?> dataType, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).count(whereClause, statementArgs);
    }

    public <T> T max(Class<?> dataType, String field, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).max(field, whereClause, statementArgs);
    }

    public <T> T min(Class<?> dataType, String field, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).min(field, whereClause, statementArgs);
    }

    public BigDecimal avg(Class<?> dataType, String field, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).avg(field, whereClause, statementArgs);
    }

    public BigDecimal sum(Class<?> dataType, String field, String whereClause, Object... statementArgs) {
        return getDataAccessChannel(dataType).sum(field, whereClause, statementArgs);
    }

    public DataAccessChannel getDataAccessChannel(Class<?> dataType) {
        return dataAccessChannelFactory.getDataAccessChannel(dataType);
    }

    public DataAccessChannel getDataAccessChannel(Class<?> dataType, String tableName) {
        return dataAccessChannelFactory.getDataAccessChannel(dataType, tableName);
    }

    public ClassTable getClassTable(Class<?> dataType) {
        return getDataAccessChannel(dataType).getClassTable();
    }

    public ClassTable getClassTable(Class<?> dataType, String tableName) {
        return getDataAccessChannel(dataType, tableName).getClassTable();
    }

    public Table getTable(Class<?> dataType) {
        return getClassTable(dataType).getTable();
    }

    public Table getTable(String tableName) {
        return dataAccessChannelFactory.getTable(tableName);
    }

    public CommonDaoConfig getCommonDaoConfig() {
        return commonDaoConfig;
    }

    public void setCommonDaoConfig(CommonDaoConfig commonDaoConfig) {
        this.commonDaoConfig = commonDaoConfig;
    }

    public void init() {
        if (commonDaoConfig == null) {
            throw new CommonDaoException("The property 'commonDaoConfig' is required.");
        }
        dataAccessChannelFactory = new DataAccessChannelFactory(commonDaoConfig);
    }

    protected Map<Class<?>, List<Object>> groupDataObjects(Object[] dataObjects) {
        Map<Class<?>, List<Object>> dataMap = new HashMap<Class<?>, List<Object>>();
        for (int i = 0, j = dataObjects.length; i < j; i++) {
            Object dataObject = dataObjects[i];
            Class<?> dataType = dataObject.getClass();
            List<Object> list = dataMap.get(dataType);
            if (list == null) {
                dataMap.put(dataType, list = new ArrayList<Object>(j - i));
            }
            list.add(dataObject);
        }
        return dataMap;
    }

    protected Map<Class<?>, List<Object>> groupDataList(List<?> dataObjects) {
        Map<Class<?>, List<Object>> dataMap = new HashMap<Class<?>, List<Object>>();
        int size = dataObjects.size();
        int index = 0;
        for (Object dataObject : dataObjects) {
            Class<?> dataType = dataObject.getClass();
            List<Object> list = dataMap.get(dataType);
            if (list == null) {
                dataMap.put(dataType, list = new ArrayList<Object>(size - index));
            }
            list.add(dataObject);
            index++;
        }
        return dataMap;
    }

    protected static void checkTableNameParam(String tableName) {
        if (tableName == null) {
            throw new CommonDaoException("Parameter 'tableName' is required.");
        }
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

}
