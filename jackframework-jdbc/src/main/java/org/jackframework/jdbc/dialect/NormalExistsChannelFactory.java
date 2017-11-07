package org.jackframework.jdbc.dialect;

import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.orm.ClassTable;

public class NormalExistsChannelFactory implements ExistsChannelFactory {

    @Override
    public ExistsChannel createExistsChannel(CommonDaoConfig config, ClassTable classTable) {
        return new NormalExistsChannel(config.getDataSource(), classTable);
    }

}
