package org.jackframework.component.utils;

import com.alibaba.fastjson.util.TypeUtils;
import org.jackframework.common.CaptainTools;
import org.jackframework.common.exceptions.RunningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class AppConfig {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    protected static final String DEFAULT_YAML_CONFIG_PATH = "application.yaml";

    protected static final String PROP_ORDER_ORDINAL = "applicationYamlOrdinal";

    protected static final Map<String, Object> PROPERTIES = new LinkedHashMap<String, Object>();

    static {
        initApplicationConfig();
    }

    public static String getString(String name, String defaultValue) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public static String requireString(String name) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            throw requiredPropertyNotFound(name);
        }
        return value.toString();
    }

    public static String getStringNotEmpty(String name, String defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isEmpty(name)) {
            return defaultValue;
        }
        return value.toString();
    }

    public static String requireStringNotEmpty(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isEmpty(name)) {
            throw requiredPropertyNotFound(name);
        }
        return value.toString();
    }

    public static String getStringNotBlank(String name, String defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(name)) {
            return defaultValue;
        }
        return value.toString();
    }

    public static String requireStringNotBlank(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(name)) {
            throw requiredPropertyNotFound(name);
        }
        return value.toString();
    }

    public static int getInt(String name, int defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToInt(value);
    }

    public static int requireInt(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToInt(value);
    }

    public static long getLong(String name, long defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToLong(value);
    }

    public static long requireLong(String name) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToLong(value);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToBoolean(value);
    }

    public static boolean requireBoolean(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToBoolean(value);
    }

    public static double getDouble(String name, long defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToDouble(value);
    }

    public static double requireDouble(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToDouble(value);
    }

    public static BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToBigDecimal(value);
    }

    public static BigDecimal requireBigDecimal(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToBigDecimal(value);
    }

    public static Date getDate(String name, Date defaultValue) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            return defaultValue;
        }
        return TypeUtils.castToDate(value);
    }

    public static Date requireDate(String name) {
        Object value = PROPERTIES.get(name);
        if (CaptainTools.isBlank(value)) {
            throw requiredPropertyNotFound(name);
        }
        return TypeUtils.castToDate(value);
    }

    public static Object get(String name, Object defaultValue) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Object require(String name) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            throw requiredPropertyNotFound(name);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAndCast(String name, T defaultValue) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAndCast(String name) {
        Object value = PROPERTIES.get(name);
        if (value == null) {
            throw requiredPropertyNotFound(name);
        }
        return (T) value;
    }

    protected static RunningException requiredPropertyNotFound(String name) {
        return new RunningException("The required [{}] configuration property could not be found.", name);
    }

    protected static void initApplicationConfig() {
        try {
            List<Map<Object, Object>> props = findClassPathYamls();

            if (props.size() == 0) {
                LOGGER.warn("Could not found the [{}] from current classpath.", DEFAULT_YAML_CONFIG_PATH);
            } else {
                LOGGER.info("Found {} [{}](s) from current classpath.", props.size(), DEFAULT_YAML_CONFIG_PATH);
            }

            Collections.reverse(props);
            props.sort(new Comparator<Map<Object, Object>>() {
                @Override
                public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                    return Double.compare(AppConfig.getPropOrdinal(o1), AppConfig.getPropOrdinal(o2));
                }
            });

            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            for (Map<Object, Object> prop : props) {
                mergeProperties(result, prop);
            }

            mergeFinalProperties(PROPERTIES, result);
        } catch (Throwable e) {
            throw new RunningException("Initialized the [{}] error.", DEFAULT_YAML_CONFIG_PATH, e);
        }
    }

    protected static List<Map<Object, Object>> findClassPathYamls() throws IOException {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        if (classLoader == null) {
            LOGGER.warn("The [ClassLoader] of system isn't accessible.");
            return Collections.emptyList();
        }

        Enumeration<URL> resources = classLoader.getResources(DEFAULT_YAML_CONFIG_PATH);

        List<Map<Object, Object>> props = new ArrayList<Map<Object, Object>>();
        while (resources.hasMoreElements()) {
            props.add(asMap(loadYaml(resources.nextElement())));
        }

        return props;
    }

    @SuppressWarnings("unchecked")
    protected static Map<Object, Object> asMap(Object object) {
        // YAML can have numbers as keys
        if (!(object instanceof Map)) {
            // A document can be a text literal
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("document", object);
            return result;
        }
        return (Map<Object, Object>) object;
    }

    @SuppressWarnings("unchecked")
    protected static void mergeProperties(Map<Object, Object> result, Map<Object, Object> props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            Object key = entry.getKey();
            Object oldValue = result.get(key);
            Object newValue = entry.getValue();
            if (oldValue == null) {
                result.put(key, newValue);
                continue;
            }
            if (oldValue instanceof Map && newValue instanceof Map) {
                mergeMapProperties(toString(key), (Map<Object, Object>) oldValue, (Map<Object, Object>) newValue);
                continue;
            }
            LOGGER.info("Duplicate config [{}] was be found, old: {}, new: {}", toString(key), oldValue, newValue);
            result.put(key, newValue);
        }
    }

    @SuppressWarnings("unchecked")
    protected static void mergeMapProperties(String prefix, Map<Object, Object> oldMap, Map<Object, Object> newMap) {
        for (Map.Entry<Object, Object> entry : newMap.entrySet()) {
            Object key = entry.getKey();
            Object oldValue = oldMap.get(key);
            Object newValue = entry.getValue();
            if (oldValue == null) {
                oldMap.put(key, newValue);
                continue;
            }
            if (oldValue instanceof Map && newValue instanceof Map) {
                mergeMapProperties(toString(prefix, key),
                                   (Map<Object, Object>) oldValue, (Map<Object, Object>) newValue);
                continue;
            }
            LOGGER.info("Duplicate config [{}] was be found, old: {}, new: {}",
                        toString(prefix, key), oldValue, newValue);
            oldMap.put(key, newValue);
        }
    }

    @SuppressWarnings("unchecked")
    protected static void mergeFinalProperties(Map<String, Object> result, Map<Object, Object> prop) {
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            mergeFinalKeyValue(result, toString(entry.getKey()), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    protected static Object mergeFinalKeyValue(Map<String, Object> result, String key, Object value) {
        if (value instanceof Map) {
            value = mergeFinalMap(key, result, (Map<Object, Object>) value);
        }
        if (value instanceof Collection) {
            mergeFinalCollection(key, result, (Collection<Object>) value);
        }
        result.put(key, value);
        return value;
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> mergeFinalMap(String prefix,
                                                       Map<String, Object> result, Map<Object, Object> map) {
        Map<String, Object> mergeProps = new LinkedHashMap<String, Object>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            mergeProps.put(toString(entry.getKey()),
                           mergeFinalKeyValue(result, toString(prefix, entry.getKey()), entry.getValue()));
        }
        return mergeProps;
    }

    @SuppressWarnings("unchecked")
    protected static void mergeFinalCollection(String prefix,
                                               Map<String, Object> result, Collection<Object> collection) {
        int index = 0;
        for (Object item : collection) {
            mergeFinalKeyValue(result, prefix + '[' + (index++) + ']', item);
        }
    }

    protected static String toString(Object key) {
        if (key instanceof CharSequence) {
            return key.toString();
        } else {
            // It has to be a map key in this case
            return '[' + key.toString() + ']';
        }
    }

    protected static String toString(String prefix, Object key) {
        if (key instanceof CharSequence) {
            return prefix + '.' + key.toString();
        } else {
            // It has to be a map key in this case
            return prefix + '[' + key.toString() + ']';
        }
    }

    protected static Object loadYaml(URL url) {
        Yaml yaml = new Yaml();
        InputStream is = null;
        try {
            return yaml.load(is = url.openStream());
        } catch (IOException e) {
            LOGGER.warn("Loaded the [{}] error - {}", DEFAULT_YAML_CONFIG_PATH, url, e);
        } finally {
            CaptainTools.close(is);
        }
        return Collections.emptyMap();
    }

    protected static double getPropOrdinal(Map<Object, Object> properties) {
        Object ordinal = properties.get(PROP_ORDER_ORDINAL);
        if (CaptainTools.isBlank(ordinal)) {
            return 0D;
        }
        try {
            return TypeUtils.castToDouble(ordinal);
        } catch (Throwable e) {
            return 0D;
        }
    }

}
