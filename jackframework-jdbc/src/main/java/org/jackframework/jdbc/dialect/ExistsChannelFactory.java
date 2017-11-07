package org.jackframework.jdbc.dialect;

import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.orm.ClassTable;

public interface ExistsChannelFactory {

    ExistsChannel createExistsChannel(CommonDaoConfig config, ClassTable classTable);

}
