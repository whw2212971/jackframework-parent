package org.jackframework.jdbc.orm;

import org.jackframework.common.reflect.FastMethod;
import org.jackframework.jdbc.core.CommonDaoException;
import org.jackframework.jdbc.jdbc.JdbcUtils;
import org.jackframework.jdbc.jdbc.ResultGetter;
import org.jackframework.jdbc.jdbc.StatementSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldColumn {

    protected Column          column;
    protected Field           field;
    protected FastMethod      getter;
    protected FastMethod      setter;
    protected StatementSetter statementSetter;
    protected ResultGetter    resultGetter;

    public FieldColumn(Column column, Field field, Method getterMethod, Method setterMethod) {
        try {
            this.column = column;
            this.field = field;

            this.getter = FastMethod.getFastMethod(getterMethod);
            this.setter = FastMethod.getFastMethod(setterMethod);

            Class<?> fieldType = field.getType();
            this.statementSetter = JdbcUtils.getStatementSetter(fieldType);
            this.resultGetter = JdbcUtils.getResultGetter(fieldType);
        } catch (Throwable e) {
            throw new CommonDaoException(e, "Cause of the field: {}", field.toGenericString());
        }
    }

    public void setValue(Object target, Object value) {
        setter.invoke(target, value);
    }

    public Object getValue(Object target) {
        return getter.invoke(target);
    }

    public void setStatementValue(PreparedStatement statement, int index, Object target) throws SQLException {
        Object value = getValue(target);
        if (value == null) {
            statement.setString(index, null);
            return;
        }
        statementSetter.setValue(statement, index, value);
    }

    public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
        return resultGetter.getResultValue(resultSet, index);
    }

    public void setResultValue(ResultSet resultSet, int index, Object target) throws SQLException {
        resultGetter.setResultValue(resultSet, index, setter, target);
    }

    public String getColumnName() {
        return getColumn().getColumnName();
    }

    public String getFieldName() {
        return getField().getName();
    }

    public Column getColumn() {
        return column;
    }

    public Field getField() {
        return field;
    }

    public FastMethod getGetter() {
        return getter;
    }

    public FastMethod getSetter() {
        return setter;
    }

    public StatementSetter getStatementSetter() {
        return statementSetter;
    }

    public ResultGetter getResultGetter() {
        return resultGetter;
    }

}