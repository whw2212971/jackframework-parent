package org.jackframework.jdbc.translators;

import org.jackframework.common.CharsWriter;

import java.lang.reflect.Field;

public class CamelUpperSnakeTranslator implements NameTranslator {

    @Override
    public String classToTable(Class<?> dataType) {
        return camelToUpperSnake(dataType.getSimpleName());
    }

    @Override
    public String fieldToColumn(Field field) {
        return camelToUpperSnake(field.getName());
    }

    public static String camelToUpperSnake(String name) {
        CharsWriter cbuf   = new CharsWriter();
        int         length = name.length();
        if (0 < length) {
            int codePoint = name.charAt(0);
            if (codePoint <= 'Z' && codePoint >= 'A') {
                cbuf.write(codePoint);
            } else {
                cbuf.write(Character.toUpperCase(codePoint));
            }
            int off = 1;
            while (off < length) {
                codePoint = name.charAt(off++);
                if (codePoint <= 'Z' && codePoint >= 'A') {
                    cbuf.append('_').write(codePoint);
                } else {
                    cbuf.write(Character.toUpperCase(codePoint));
                }
            }
        }
        return cbuf.closeToString();
    }

}