package org.jackframework.jdbc.core;

import java.util.List;
import java.util.Map;

public class CommonDao {

    public <T> void insert(T dataObject) {

    }

    public <T> void insert(T... dataObjects) {

    }

    public <T extends List<?>> void insert(T dataObjects) {

    }

    public int delete(Class<?> dataType, Object id) {
        return 0;
    }

    public int delete(Class<?> dataType, String whereClause, Object... statementArgs) {
        return 0;
    }

    public int update(Object dataObject) {
        return 0;
    }

    public int update(Class<?> dataObject, String updateClause, Object... statementArgs) {
        return 0;
    }

    public int updateOptimized(Object dataObject) {
        return 0;
    }

    public int updateOptimized(Object dataObject, String... forceUpdateFields) {
        return 0;
    }

    public <T> T findOne(Class<T> dataType, Object id) {
        return null;
    }

    public <T> T findOne(Class<T> dataType, Object id, Includes includes) {
        return null;
    }

    public <T> T findOne(Class<T> dataType, Object id, Excludes excludes) {
        return null;
    }

    public <T> T findOne(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        return null;
    }

    public <T> T findOne(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        return null;
    }

    public <T> T findOne(Class<?> dataType, String fieldName, Class<T> resultType, Object id) {
        return null;
    }

    public <T> T findOne(
            Class<?> dataType, String fieldName, Class<T> resultType, String whereClause, Object... statementArgs) {
        return null;
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id) {
        return null;
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Includes includes) {
        return null;
    }

    public Map<String, Object> findMap(Class<?> dataType, Object id, Excludes excludes) {
        return null;
    }

    public Map<String, Object> findMap(Class<?> dataType, String whereClause, Object... statementArgs) {
        return null;
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        return null;
    }

    public Map<String, Object> findMap(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        return null;
    }

    public <T> List<T> findList(Class<T> dataType, String whereClause, Object... statementArgs) {
        return null;
    }

    public <T> List<T> findList(Class<T> dataType, Includes includes, String whereClause, Object... statementArgs) {
        return null;
    }

    public <T> List<T> findList(Class<T> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        return null;
    }

    public List<Map<String, Object>> findMapList(Class<?> dataType, String whereClause, Object... statementArgs) {
        return null;
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Includes includes, String whereClause, Object... statementArgs) {
        return null;
    }

    public List<Map<String, Object>> findMapList(
            Class<?> dataType, Excludes excludes, String whereClause, Object... statementArgs) {
        return null;
    }

}
