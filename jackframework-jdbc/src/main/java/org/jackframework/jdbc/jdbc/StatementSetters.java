package org.jackframework.jdbc.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public class StatementSetters {

    public static final NullSetter          NULL_SETTER            = new NullSetter();
    public static final StringSetter        STRING_SETTER          = new StringSetter();
    public static final IntegerSetter       INTEGER_SETTER         = new IntegerSetter();
    public static final LongSetter          LONG_SETTER            = new LongSetter();
    public static final BooleanSetter       BOOLEAN_SETTER         = new BooleanSetter();
    public static final JUtilDateSetter     J_UTIL_DATE_SETTER     = new JUtilDateSetter();
    public static final JSqlDateSetter      J_SQL_DATE_SETTER      = new JSqlDateSetter();
    public static final JSqlTimestampSetter J_SQL_TIMESTAMP_SETTER = new JSqlTimestampSetter();
    public static final JSqlTimeSetter      J_SQL_TIME_SETTER      = new JSqlTimeSetter();
    public static final BigDecimalSetter    BIG_DECIMAL_SETTER     = new BigDecimalSetter();
    public static final BigIntegerSetter    BIG_INTEGER_SETTER     = new BigIntegerSetter();
    public static final DoubleSetter        DOUBLE_SETTER          = new DoubleSetter();
    public static final FloatSetter         FLOAT_SETTER           = new FloatSetter();
    public static final ByteSetter          BYTE_SETTER            = new ByteSetter();
    public static final ShortSetter         SHORT_SETTER           = new ShortSetter();

    public static class NullSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setString(index, null);
        }

    }

    public static class StringSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setString(index, value.toString());
        }

    }

    public static class IntegerSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setInt(index, (Integer) value);
        }

    }

    public static class LongSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setLong(index, (Long) value);
        }

    }

    public static class BooleanSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setBoolean(index, (Boolean) value);
        }

    }

    public static class JUtilDateSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        }

    }

    public static class JSqlDateSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setDate(index, (java.sql.Date) value);
        }

    }

    public static class JSqlTimestampSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setTimestamp(index, (Timestamp) value);
        }

    }

    public static class JSqlTimeSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setTime(index, (Time) value);
        }

    }

    public static class BigDecimalSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setBigDecimal(index, (BigDecimal) value);
        }

    }

    public static class BigIntegerSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }

    }

    public static class DoubleSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setDouble(index, (Double) value);
        }

    }

    public static class FloatSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setFloat(index, (Float) value);
        }

    }

    public static class ByteSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setByte(index, (Byte) value);
        }

    }

    public static class ShortSetter implements StatementSetter {

        @Override
        public void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setShort(index, (Short) value);
        }

    }

}
