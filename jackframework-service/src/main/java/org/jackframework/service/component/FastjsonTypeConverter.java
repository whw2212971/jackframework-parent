package org.jackframework.service.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.jackframework.common.CaptainTools;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;

public class FastjsonTypeConverter implements ServiceTypeConverter {

    protected ParserConfig parserConfig;

    protected Feature[] features;

    protected SerializerFeature[] serializerFeatures;

    protected Class<? extends FastArgumentsBean> fastArgumentsBeanClass;

    protected int paramCount;

    protected ResultWrapper resultWrapper;

    public FastjsonTypeConverter(ParserConfig parserConfig,
                                 Feature[] features, SerializerFeature[] serializerFeatures,
                                 ServiceMethodHandler handler, ResultWrapper resultWrapper) {
        this.parserConfig = parserConfig;
        this.features = features;
        this.serializerFeatures = serializerFeatures;
        this.fastArgumentsBeanClass = FastArgumentsBean.createFastArgumentsBeanClass(handler);
        this.paramCount = handler.getParamCount();
        this.resultWrapper = resultWrapper;
    }

    @Override
    public Object[] convertArguments(HttpProcessContext processContext) throws Throwable {
        InputStream in = null;
        try {
            in = processContext.getRequest().getInputStream();
            if (in == null) {
                return new Object[paramCount];
            }
            String body = CaptainTools.toString(in);
            ServiceHolder.setRequestBody(body);
            FastArgumentsBean fastArgumentsBean =
                    JSON.parseObject(body, fastArgumentsBeanClass, parserConfig, features);
            if (fastArgumentsBean == null) {
                return new Object[paramCount];
            }
            return fastArgumentsBean.getArguments();
        } finally {
            CaptainTools.close(in);
        }
    }

    @Override
    public void resolveResult(HttpProcessContext processContext, Object result) throws Throwable {
        HttpServletResponse response = processContext.getResponse();
        PrintWriter         writer   = response.getWriter();
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            JSON.writeJSONString(writer, resultWrapper.wrapResult(processContext, result), serializerFeatures);
        } finally {
            CaptainTools.close(writer);
        }
    }

}
