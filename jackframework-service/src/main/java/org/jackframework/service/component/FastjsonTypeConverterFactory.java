package org.jackframework.service.component;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastjsonTypeConverterFactory implements ServiceTypeConverterFactory {

    protected ParserConfig parserConfig;

    protected Feature[] features;

    protected SerializerFeature[] serializerFeatures;

    protected ResultWrapper resultWrapper;

    @Override
    public ServiceTypeConverter createServiceTypeConverter(ServiceMethodHandler handler) {
        if (parserConfig == null) {
            parserConfig = ParserConfig.getGlobalInstance();
        }
        if (features == null) {
            features = new Feature[0];
        }
        if (serializerFeatures == null) {
            serializerFeatures = new SerializerFeature[0];
        }
        if (resultWrapper == null) {
            resultWrapper = new FastjsonResultWrapper();
        }
        return new FastjsonTypeConverter(parserConfig, features, serializerFeatures, handler, resultWrapper);
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

    public ResultWrapper getResultWrapper() {
        return resultWrapper;
    }

    public void setResultWrapper(ResultWrapper resultWrapper) {
        this.resultWrapper = resultWrapper;
    }

}
