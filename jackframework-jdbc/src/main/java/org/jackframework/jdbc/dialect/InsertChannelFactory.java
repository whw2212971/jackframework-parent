package org.jackframework.jdbc.dialect;

import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.orm.ClassTable;

public interface InsertChannelFactory {

    InsertChannel createInsertChannel(CommonDaoConfig config, ClassTable classTable);

}
