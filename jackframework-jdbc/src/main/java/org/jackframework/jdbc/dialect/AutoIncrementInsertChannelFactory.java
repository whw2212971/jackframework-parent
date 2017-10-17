package org.jackframework.jdbc.dialect;

import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.orm.ClassTable;

public class AutoIncrementInsertChannelFactory implements InsertChannelFactory {

    protected int batchUpdateLimit = 65535;

    @Override
    public InsertChannel createInsertChannel(CommonDaoConfig config, ClassTable classTable) {
        return new AutoIncrementInsertChannel(config, classTable, batchUpdateLimit);
    }

    public int getBatchUpdateLimit() {
        return batchUpdateLimit;
    }

    public void setBatchUpdateLimit(int batchUpdateLimit) {
        this.batchUpdateLimit = batchUpdateLimit;
    }

}
