package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FastSetter {

    public abstract void setValue(Object target, Object value);

    public static FastSetter createFastSetter(Field field) {
        Method setterMethod = CaptainTools.findGetter(field);
        if (setterMethod != null) {
            return new FastMethodSetter(FastMethod.getFastMethod(setterMethod));
        }
        return new FastFieldSetter(FastField.getFastField(field));
    }

    public static class FastMethodSetter extends FastSetter {

        protected FastMethod fastMethod;

        public FastMethodSetter(FastMethod fastMethod) {
            this.fastMethod = fastMethod;
        }

        @Override
        public void setValue(Object target, Object value) {
            fastMethod.invoke(target, value);
        }

        @Override
        public String toString() {
            return "FastMethodSetter: " + fastMethod.getMethod().toGenericString();
        }

    }

    public static class FastFieldSetter extends FastSetter {

        protected FastField fastField;

        public FastFieldSetter(FastField fastField) {
            this.fastField = fastField;
        }

        @Override
        public void setValue(Object target, Object value) {
            fastField.set(target, value);
        }

        @Override
        public String toString() {
            return "FastFieldSetter: " + fastField.getField().toGenericString();
        }

    }

}
