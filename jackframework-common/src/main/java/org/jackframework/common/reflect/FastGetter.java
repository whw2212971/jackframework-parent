package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FastGetter {

    public abstract Object getValue(Object target);

    public static FastGetter createFastGetter(Field field) {
        Method getterMethod = CaptainTools.findGetter(field);
        if (getterMethod != null) {
            return new FastMethodGetter(FastMethod.getFastMethod(getterMethod));
        }
        return new FastFieldGetter(FastField.getFastField(field));
    }

    public static class FastMethodGetter extends FastGetter {

        protected FastMethod fastMethod;

        public FastMethodGetter(FastMethod fastMethod) {
            this.fastMethod = fastMethod;
        }

        @Override
        public Object getValue(Object target) {
            return fastMethod.invoke(target);
        }

        @Override
        public String toString() {
            return "FastMethodGetter: " + fastMethod.getMethod().toGenericString();
        }

    }

    public static class FastFieldGetter extends FastGetter {

        protected FastField fastField;

        public FastFieldGetter(FastField fastField) {
            this.fastField = fastField;
        }

        @Override
        public Object getValue(Object target) {
            return fastField.get(target);
        }

        @Override
        public String toString() {
            return "FastFieldGetter: " + fastField.getField().toGenericString();
        }

    }

}
