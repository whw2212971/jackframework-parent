package org.jackframework.jdbc.translators;

import java.lang.reflect.Field;

public class SameTranslator implements NameTranslator {

    @Override
    public String classToTable(Class<?> dataType) {
        return dataType.getSimpleName();
    }

    @Override
    public String fieldToColumn(Field field) {
        return field.getName();
    }

}
