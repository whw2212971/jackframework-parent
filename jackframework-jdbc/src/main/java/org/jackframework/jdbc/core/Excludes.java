package org.jackframework.jdbc.core;

import org.jackframework.jdbc.orm.FieldColumn;

import java.util.HashSet;
import java.util.Set;

public class Excludes {

    protected Set<String> fieldNames;

    protected Excludes(Set<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public boolean contains(FieldColumn fieldColumn) {
        return fieldNames.contains(fieldColumn.getColumnName()) || fieldNames.contains(fieldColumn.getFieldName());
    }

    public String toExcludesString() {
        return Includes.toString(fieldNames);
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

}
