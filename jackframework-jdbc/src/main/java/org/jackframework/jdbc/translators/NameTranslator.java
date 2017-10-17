package org.jackframework.jdbc.translators;

import java.lang.reflect.Field;

public interface NameTranslator {

    String classToTable(Class<?> dataType);

    String fieldToColumn(Field field);

}
