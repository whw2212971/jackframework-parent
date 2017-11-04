package org.jackframework.service.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.jackframework.common.CaptainTools;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.lang.reflect.Type;

public class FastJsonHttpMessageConverter extends com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass,
                       HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return super.read(type, contextClass, inputMessage);
    }

    protected Object readType(Type type, HttpInputMessage inputMessage) {
        try {
            String body = CaptainTools.toString(inputMessage.getBody());
            ServiceHolder.setRequestBody(body);
            return JSON.parseObject(body, type, getFastJsonConfig().getFeatures());
        } catch (JSONException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
        }
    }

}
