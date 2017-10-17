package org.jackframework.jdbc.core;

import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.orm.FieldColumn;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Includes {

    protected Set<String> fieldNames;

    protected Includes(Set<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public boolean contains(FieldColumn fieldColumn) {
        return fieldNames.contains(fieldColumn.getColumnName()) ||
                fieldNames.contains(fieldColumn.getFieldName());
    }

    public String toIncludesString() {
        return toString(fieldNames);
    }

    @SuppressWarnings({"UseBulkOperation", "ManualArrayToCollectionCopy"})
    public static Includes include(String fieldName, String... otherNames) {
        Set<String> fieldNames = new HashSet<String>();
        fieldNames.add(fieldName);
        for (String name : otherNames) {
            fieldNames.add(name);
        }
        return new Includes(fieldNames);
    }

    protected static String toString(Set<String> fieldNames) {
        CharsWriter      cbuf     = new CharsWriter();
        Iterator<String> iterator = fieldNames.iterator();
        if (iterator.hasNext()) {
            cbuf.write(iterator.next());
            while (iterator.hasNext()) {
                cbuf.append(',');
                cbuf.append(iterator.next());
            }
        }
        return cbuf.closeToString();
    }

}
