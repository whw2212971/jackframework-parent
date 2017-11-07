package org.jackframework.jdbc.core;

import org.jackframework.jdbc.dialect.*;
import org.jackframework.jdbc.translators.CamelLowerSnakeTranslator;
import org.jackframework.jdbc.translators.NameTranslator;

import javax.sql.DataSource;

public class CommonDaoConfig {

    protected DataSource dataSource;

    protected InsertChannelFactory insertChannelFactory = new AutoIncrementInsertChannelFactory();

    protected ExistsChannelFactory existsChannelFactory = new NormalExistsChannelFactory();

    protected NameTranslator nameTranslator = new CamelLowerSnakeTranslator();

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

    public ExistsChannelFactory getExistsChannelFactory() {
        return existsChannelFactory;
    }

    public void setExistsChannelFactory(ExistsChannelFactory existsChannelFactory) {
        this.existsChannelFactory = existsChannelFactory;
    }

    public NameTranslator getNameTranslator() {
        return nameTranslator;
    }

    public void setNameTranslator(NameTranslator nameTranslator) {
        this.nameTranslator = nameTranslator;
    }

}
