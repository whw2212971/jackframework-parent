package org.jackframework.testservice.mapper;

import com.github.pagehelper.Page;
import org.jackframework.testservice.pojo.TData;

public interface NormalMapper {

    Page<TData> findDataPage();

}
