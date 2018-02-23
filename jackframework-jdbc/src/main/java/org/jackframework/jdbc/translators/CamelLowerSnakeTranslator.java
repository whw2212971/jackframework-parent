package org.jackframework.jdbc.translators;

import org.jackframework.common.CharsWriter;

import java.lang.reflect.Field;

public class CamelLowerSnakeTranslator implements NameTranslator {

    @Override
    public String classToTable(Class<?> dataType) {
        return camelToLowerSnake(dataType.getSimpleName());
    }

    @Override
    public String fieldToColumn(Field field) {
        return camelToLowerSnake(field.getName());
    }

    public static String camelToLowerSnake(String name) {
        CharsWriter cbuf = new CharsWriter();
        int length = name.length();
        if (0 < length) {
            int codePoint = name.charAt(0);
            if (codePoint <= 'Z' && codePoint >= 'A') {
                cbuf.write(Character.toLowerCase(codePoint));
            } else {
                cbuf.write(codePoint);
            }
            int off = 1;
            while (off < length) {
                codePoint = name.charAt(off++);
                if (codePoint <= 'Z' && codePoint >= 'A') {
                    cbuf.append('_').write(Character.toLowerCase(codePoint));
                } else {
                    cbuf.write(codePoint);
                }
            }
        }
        return cbuf.closeToString();
    }

}