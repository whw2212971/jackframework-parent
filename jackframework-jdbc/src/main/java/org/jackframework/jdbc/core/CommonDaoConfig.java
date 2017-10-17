package org.jackframework.jdbc.core;

import org.jackframework.jdbc.dialect.InsertChannelFactory;
import org.jackframework.jdbc.translators.NameTranslator;

import javax.sql.DataSource;

public class CommonDaoConfig {

    protected DataSource dataSource;

    protected InsertChannelFactory insertChannelFactory;

    protected NameTranslator nameTranslator;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public InsertChannelFactory getInsertChannelFactory() {
        return insertChannelFactory;
    }

    public void setInsertChannelFactory(InsertChannelFactory insertChannelFactory) {
        this.insertChannelFactory = insertChannelFactory;
    }

    public NameTranslator getNameTranslator() {
        return nameTranslator;
    }

    public void setNameTranslator(NameTranslator nameTranslator) {
        this.nameTranslator = nameTranslator;
    }

}
