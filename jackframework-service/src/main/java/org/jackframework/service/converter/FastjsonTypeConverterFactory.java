package org.jackframework.service.converter;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Method;

public class FastjsonTypeConverterFactory implements ServiceTypeConverterFactory {

    protected ParserConfig parserConfig;

    protected Feature[] features;

    protected SerializerFeature[] serializerFeatures;

    @Override
    public ServiceTypeConverter createServiceTypeConverter(Method handlerMethod) {
        if (parserConfig == null) {
            parserConfig = ParserConfig.getGlobalInstance();
        }
        if (features == null) {
            features = new Feature[0];
        }
        if (serializerFeatures == null) {
            serializerFeatures = new SerializerFeature[0];
        }
        return new FastjsonTypeConverter(
                parserConfig, features, serializerFeatures,
                FastArgumentsBean.createFastArgumentsBeanClass(handlerMethod),
                handlerMethod.getParameterTypes().length);
    }

    public ParserConfig getParserConfig() {
        return parserConfig;
    }

    public void setParserConfig(ParserConfig parserConfig) {
        this.parserConfig = parserConfig;
    }

    public Feature[] getFeatures() {
        return features;
    }

    public void setFeatures(Feature[] features) {
        this.features = features;
    }

    public SerializerFeature[] getSerializerFeatures() {
        return serializerFeatures;
    }

    public void setSerializerFeatures(SerializerFeature[] serializerFeatures) {
        this.serializerFeatures = serializerFeatures;
    }

}
