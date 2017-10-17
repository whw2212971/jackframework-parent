package org.jackframework.jdbc.jdbc;

import org.jackframework.common.reflect.FastMethod;

import java.math.BigDecimal;
import java.sql.*;

public class ResultGetters {

    public static final StringResultGetter        STRING_RESULT_GETTER          = new StringResultGetter();
    public static final IntegerResultGetter       INTEGER_RESULT_GETTER         = new IntegerResultGetter();
    public static final LongResultGetter          LONG_RESULT_GETTER            = new LongResultGetter();
    public static final BooleanResultGetter       BOOLEAN_RESULT_GETTER         = new BooleanResultGetter();
    public static final JUtilDateResultGetter     J_UTIL_DATE_RESULT_GETTER     = new JUtilDateResultGetter();
    public static final JSqlDateResultGetter      J_SQL_DATE_RESULT_GETTER      = new JSqlDateResultGetter();
    public static final JSqlTimestampResultGetter J_SQL_TIMESTAMP_RESULT_GETTER = new JSqlTimestampResultGetter();
    public static final JSqlTimeResultGetter      J_SQL_TIME_RESULT_GETTER      = new JSqlTimeResultGetter();
    public static final BigDecimalResultGetter    BIG_DECIMAL_RESULT_GETTER     = new BigDecimalResultGetter();
    public static final BigIntegerResultGetter    BIG_INTEGER_RESULT_GETTER     = new BigIntegerResultGetter();
    public static final DoubleResultGetter        DOUBLE_RESULT_GETTER          = new DoubleResultGetter();
    public static final FloatResultGetter         FLOAT_RESULT_GETTER           = new FloatResultGetter();
    public static final ByteResultGetter          BYTE_RESULT_GETTER            = new ByteResultGetter();
    public static final ShortResultGetter         SHORT_RESULT_GETTER           = new ShortResultGetter();
    public static final CharacterResultGetter     CHARACTER_RESULT_GETTER       = new CharacterResultGetter();

    public static class StringResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            Object result = resultSet.getString(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            Object result = resultSet.getString(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class IntegerResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            int result = resultSet.getInt(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            int result = resultSet.getInt(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class LongResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            long result = resultSet.getLong(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            long result = resultSet.getLong(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class BooleanResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            boolean result = resultSet.getBoolean(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            boolean result = resultSet.getBoolean(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class JUtilDateResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            Timestamp result = resultSet.getTimestamp(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return new java.util.Date(result.getTime());
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            Timestamp result = resultSet.getTimestamp(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, new java.util.Date(result.getTime()));
            }
        }

    }

    public static class JSqlDateResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            Date result = resultSet.getDate(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            Date result = resultSet.getDate(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class JSqlTimestampResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            Timestamp result = resultSet.getTimestamp(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            Timestamp result = resultSet.getTimestamp(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class JSqlTimeResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            Time result = resultSet.getTime(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            Time result = resultSet.getTime(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class BigDecimalResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            BigDecimal result = resultSet.getBigDecimal(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            BigDecimal result = resultSet.getBigDecimal(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class BigIntegerResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            BigDecimal result = resultSet.getBigDecimal(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result.toBigInteger();
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            BigDecimal result = resultSet.getBigDecimal(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result.toBigInteger());
            }
        }

    }

    public static class DoubleResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            double result = resultSet.getDouble(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            double result = resultSet.getDouble(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class FloatResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            float result = resultSet.getFloat(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            float result = resultSet.getFloat(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class ByteResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            byte result = resultSet.getByte(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            byte result = resultSet.getByte(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class ShortResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            short result = resultSet.getShort(index);
            if (resultSet.wasNull()) {
                return null;
            }
            return result;
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            short result = resultSet.getShort(index);
            if (!resultSet.wasNull()) {
                setter.invoke(target, result);
            }
        }

    }

    public static class CharacterResultGetter implements ResultGetter {

        @Override
        public Object getResultValue(ResultSet resultSet, int index) throws SQLException {
            String result = resultSet.getString(index);
            if (resultSet.wasNull() || result.length() == 0) {
                return null;
            }
            return result.charAt(0);
        }

        @Override
        public void setResultValue(
                ResultSet resultSet, int index, FastMethod setter, Object target) throws SQLException {
            String result = resultSet.getString(index);
            if (!resultSet.wasNull() && result.length() > 0) {
                setter.invoke(target, result.charAt(0));
            }
        }

    }

}
