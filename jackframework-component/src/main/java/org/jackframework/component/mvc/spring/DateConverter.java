package org.jackframework.component.mvc.spring;

import com.alibaba.fastjson.util.TypeUtils;
import org.jackframework.common.CaptainTools;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    @Nullable
    @Override
    public Date convert(String source) {
        if (CaptainTools.isBlank(source)) {
            return null;
        }
        return TypeUtils.castToDate(source);
    }

}
