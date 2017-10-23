package org.jackframework.jdbc.jdbc;

import org.jackframework.common.CaptainTools;
import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.orm.ClassTable;
import org.jackframework.jdbc.orm.Column;
import org.jackframework.jdbc.orm.FieldColumn;
import org.jackframework.jdbc.orm.Table;
import org.jackframework.jdbc.translators.NameTranslator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataAccessChannelFactory {

    protected CommonDaoConfig commonDaoConfig;

    protected Map<Class<?>, DataAccessChannel> dataAccessChannelMap;

    public DataAccessChannelFactory(CommonDaoConfig commonDaoConfig) {
        this.commonDaoConfig = commonDaoConfig;
        this.dataAccessChannelMap = new ConcurrentHashMap<Class<?>, DataAccessChannel>(1024);
    }

    public DataAccessChannel getDataAccessChannel(Class<?> dataType) {
        DataAccessChannel dataAccessChannel = dataAccessChannelMap.get(dataType);
        if (dataAccessChannel == null) {
            synchronized (this) {
                dataAccessChannel = dataAccessChannelMap.get(dataType);
                if (dataAccessChannel == null) {
                    dataAccessChannelMap.put(dataType, dataAccessChannel = createDataAccessChannel(dataType));
                }
            }
        }
        return dataAccessChannel;
    }

    public DataAccessChannel createDataAccessChannel(Class<?> dataType) {
        CommonDaoConfig commonDaoConfig = this.commonDaoConfig;
        NameTranslator  nameTranslator  = commonDaoConfig.getNameTranslator();
        Table           table           = createTable(nameTranslator.classToTable(dataType));
        ClassTable      classTable      = createClassTable(dataType, table);
        return new DataAccessChannel(commonDaoConfig, classTable);
    }

    public Table createTable(String tableName) {
        Connection connection    = null;
        ResultSet  primaryKeySet = null;
        ResultSet  columnSet     = null;
        try {
            connection = commonDaoConfig.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String           catalog  = connection.getCatalog();
            List<Column>     columns  = new ArrayList<Column>();
            Table            table    = new Table(tableName, columns);

            columnSet = metaData.getColumns(catalog, null, tableName, "%");
            columns.add(null);
            String primaryName;
            if (columnSet.next()) {
                primaryKeySet = metaData.getPrimaryKeys(catalog, null, tableName);
                if (primaryKeySet.next()) {
                    primaryName = primaryKeySet.getString("COLUMN_NAME");
                    if (primaryKeySet.next()) {
                        throw new CommonDaoException(
                                "Union primary key is not supported, table name: {}, catalog: {}.", tableName, catalog);
                    }
                } else {
                    throw new CommonDaoException(
                            "There is no primary key, table name: {}, catalog: {}.", tableName, catalog);
                }
                String name = columnSet.getString("COLUMN_NAME");
                if (primaryName.equals(name)) {
                    columns.set(0, new Column(name, columnSet.getInt("DATA_TYPE"), true, table));
                } else {
                    columns.add(new Column(name, columnSet.getInt("DATA_TYPE"), true, table));
                }
            } else {
                throw new CommonDaoException(
                        "Could not found the table, table name: {}, catalog: {}.", tableName, catalog);
            }
            while (columnSet.next()) {
                String name = columnSet.getString("COLUMN_NAME");
                if (primaryName.equals(name)) {
                    columns.set(0, new Column(name, columnSet.getInt("DATA_TYPE"), true, table));
                    continue;
                }
                columns.add(new Column(name, columnSet.getInt("DATA_TYPE"), true, table));
            }
            return table;
        } catch (Throwable e) {
            throw new CommonDaoException(e);
        } finally {
            JdbcUtils.closeQuietly(primaryKeySet);
            JdbcUtils.closeQuietly(columnSet);
            JdbcUtils.closeQuietly(connection);
        }
    }

    public ClassTable createClassTable(Class<?> dataType, Table table) {
        NameTranslator     nameTranslator = commonDaoConfig.getNameTranslator();
        Map<String, Field> fieldMap       = new HashMap<String, Field>();
        Field[]            fields         = dataType.getDeclaredFields();

        for (Field field : fields) {
            fieldMap.put(nameTranslator.fieldToColumn(field), field);
        }

        Column column = table.getColumn(0);
        String pkName = column.getColumnName();

        Field field = fieldMap.get(pkName);
        if (field == null) {
            throw new CommonDaoException("Could not found the primary field, column: '{}'.", pkName);
        }

        Method getter = CaptainTools.findGetter(field);
        if (getter == null) {
            throw new CommonDaoException("Could not found the getter, field: '{}'.", field.toGenericString());
        }

        Method setter = CaptainTools.findSetter(field);
        if (setter == null) {
            throw new CommonDaoException("Could not found the setter, field: '{}'.", field.toGenericString());
        }

        int               length       = table.getColumnsCount();
        List<FieldColumn> fieldColumns = new ArrayList<FieldColumn>(length);

        fieldColumns.add(new FieldColumn(column, field, getter, setter));

        for (int i = 1; i < length; i++) {
            column = table.getColumn(i);
            field = fieldMap.get(column.getColumnName());
            if (field == null) {
                continue;
            }
            if ((getter = CaptainTools.findGetter(field)) == null) {
                throw new CommonDaoException("Could not found the getter, field: '{}'.", field.toGenericString());
            }
            if ((setter = CaptainTools.findSetter(field)) == null) {
                throw new CommonDaoException("Could not found the setter, field: '{}'.", field.toGenericString());
            }
            fieldColumns.add(new FieldColumn(column, field, getter, setter));
        }

        return new ClassTable(table, dataType, fieldColumns.toArray(new FieldColumn[fieldColumns.size()]));
    }

}
