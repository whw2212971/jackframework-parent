package org.jackframework.jdbc.orm;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.reflect.FastMethod;
import org.jackframework.jdbc.core.CommonDaoException;
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

    public FieldColumn(Column column, Field field) {
        this.column = column;
        this.field = field;

        Method getterMethod = CaptainTools.findGetter(field);
        if (getterMethod == null) {
            throw new CommonDaoException("Could not found the getter: {}", field.toGenericString());
        }

        Method setterMethod = CaptainTools.findGetter(field);
        if (setterMethod == null) {
            throw new CommonDaoException("Could not found the setter: {}", field.toGenericString());
        }

        this.getter = FastMethod.getFastMethod(getterMethod);
        this.setter = FastMethod.getFastMethod(setterMethod);
    }

    public void setValue(Object target, Object value) {
        setter.invoke(target, value);
    }

    public Object getValue(Object target) {
        return getter.invoke(target);
    }

    public void setStatementValue(PreparedStatement statement, int index, Object target) throws SQLException {
        statementSetter.setValue(statement, index, getValue(target));
    }

    public Object getResultValue(ResultSet resultSet, int index) {
        return resultGetter.getResultValue(resultSet, index);
    }

    public void setResultValue(ResultSet resultSet, int index, Object target) {
        setValue(target, getResultValue(resultSet, index));
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