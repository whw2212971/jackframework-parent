package org.jackframework.common;

import org.jackframework.common.exceptions.RunningException;
import org.jackframework.common.exceptions.WrappedRunningException;

import java.io.*;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CaptainTools {

    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static final
    ThreadLocal<SoftReference<char[]>> THREAD_CHAR_BUFFER_CACHE = new ThreadLocal<SoftReference<char[]>>();
    public static final int DEFAULT_THREAD_CHAR_BUFFER_SIZE       = 1024;
    public static final int MAX_THREAD_REFERENCE_CHAR_BUFFER_SIZE = DEFAULT_THREAD_CHAR_BUFFER_SIZE * 8;

    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    protected static final char[] MIN_INT_VALUE  = "-2147483648".toCharArray();
    protected static final char[] MIN_LONG_VALUE = "-9223372036854775808".toCharArray();

    protected static final int[]  INT_SIZE_TABLE = {
            9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    protected static final char[] DIGIT_TENS     = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
    protected static final char[] DIGIT_ONES     = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    protected static final AtomicLong ATOMIC_LONG = new AtomicLong();

    public static String formatMessage(String message, Object... arguments) {
        if (message == null) {
            return null;
        }

        int length   = message.length();
        int index    = 0;
        int prev     = 0;
        int argIndex = 0;
        int codePoint;

        CharsWriter cbuf = new CharsWriter(length + 50);

        OUT:
        while (index < length) {
            codePoint = message.charAt(index);
            if (codePoint == '{') {
                if (++index < length) {
                    if (message.charAt(index) == '}') {
                        cbuf.appendSubstring(message, prev, index - 1).write(arguments[argIndex++]);
                        prev = ++index;
                    }
                    continue;
                }
                break;
            } else if (codePoint == '\\') {
                while (++index < length) {
                    codePoint = message.charAt(index);
                    if (codePoint == '{') {
                        cbuf.appendSubstring(message, prev, index - 1).write('{');
                        prev = ++index;
                        continue OUT;
                    } else if (codePoint == '\\') {
                        continue;
                    }
                    index++;
                    continue OUT;
                }
                break;
            }
            index++;
        }

        if (prev < length) {
            cbuf.appendSubstring(message, prev, length);
        }

        return cbuf.closeToString();
    }

    public static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                Modifier.isPublic(method.getDeclaringClass().getModifiers()) &&
                Modifier.isPublic(method.getReturnType().getModifiers()) &&
                isPublic(method.getParameterTypes());
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers()) &&
                Modifier.isPublic(field.getDeclaringClass().getModifiers());
    }

    public static boolean isPublic(Constructor<?> constructor) {
        return Modifier.isPublic(constructor.getModifiers()) &&
                Modifier.isPublic(constructor.getDeclaringClass().getModifiers()) &&
                isPublic(constructor.getParameterTypes());
    }

    public static boolean isPublic(Class<?>... paramTypes) {
        for (Class<?> type : paramTypes) {
            if (Modifier.isPublic(type.getModifiers())) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean isPublic(Type... paramTypes) {
        for (Type type : paramTypes) {
            if (Modifier.isPublic(getTypeClass(type).getModifiers())) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static Method tryGetMethod(Class<?> classType, String methodName, Class<?>... paramTypes) {
        try {
            return classType.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Constructor<T> tryGetConstructor(Class<T> classType, Class<?>... paramTypes) {
        try {
            return classType.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findGetter(Field field) {
        String fieldName = field.getName();

        // x  -> getX
        // xx -> getXx
        String   getterName   = getGetterName(fieldName);
        Class<?> classType    = field.getDeclaringClass();
        Class<?> fieldType    = field.getType();
        Method   getterMethod = tryGetMethod(classType, getterName);

        if (getterMethod == null && (fieldType == boolean.class || fieldType == Boolean.class)) {
            if (fieldName.length() == 1) {
                // x|X   -> isX
                getterName = "is" + fieldName.toUpperCase();
            } else {
                // isXxx -> isXxx
                // Xxx   -> isXxx
                // xxx   -> isXxx
                String sub = fieldName.substring(0, 2).toLowerCase();
                getterName = sub.equals("is") ? sub + fieldName.substring(2) :
                        "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            }
            getterMethod = tryGetMethod(classType, getterName);
        }
        if (getterMethod != null && fieldType == getterMethod.getReturnType()) {
            return getterMethod;
        }
        return null;
    }

    public static Method findSetter(Field field) {
        String fieldName = field.getName();

        // x  -> setX
        // xx -> setXx
        String   getterName   = getSetterName(fieldName);
        Class<?> classType    = field.getDeclaringClass();
        Class<?> fieldType    = field.getType();
        Method   getterMethod = tryGetMethod(classType, getterName, fieldType);

        if (getterMethod == null && (fieldType == boolean.class || fieldType == Boolean.class) &&
                fieldName.length() > 1 && fieldName.substring(0, 2).toLowerCase().equals("is")) {
            // isXxx -> setXxx
            return tryGetMethod(classType, "set" + fieldName.substring(2), fieldType);
        }
        return getterMethod;
    }

    public static String getGetterName(String fieldName) {
        return "get" + (fieldName.length() == 1 ? fieldName.toUpperCase() :
                Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
    }

    public static String getSetterName(String fieldName) {
        return "set" + (fieldName.length() == 1 ? fieldName.toUpperCase() :
                Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
    }

    public static Class<?> getPackingClass(Class<?> primitiveClass) {
        if (primitiveClass == int.class) {
            return Integer.class;
        } else if (primitiveClass == boolean.class) {
            return Boolean.class;
        } else if (primitiveClass == long.class) {
            return Long.class;
        } else if (primitiveClass == double.class) {
            return Double.class;
        } else if (primitiveClass == float.class) {
            return Float.class;
        } else if (primitiveClass == char.class) {
            return Character.class;
        } else if (primitiveClass == short.class) {
            return Short.class;
        } else if (primitiveClass == byte.class) {
            return Byte.class;
        }
        return primitiveClass;
    }

    public static Method getUnpackingMethod(Class<?> primitiveClass) {
        try {
            if (primitiveClass == int.class) {
                return Integer.class.getMethod("intValue");
            } else if (primitiveClass == boolean.class) {
                return Boolean.class.getMethod("booleanValue");
            } else if (primitiveClass == long.class) {
                return Long.class.getMethod("longValue");
            } else if (primitiveClass == double.class) {
                return Double.class.getMethod("doubleValue");
            } else if (primitiveClass == float.class) {
                return Float.class.getMethod("floatValue");
            } else if (primitiveClass == char.class) {
                return Character.class.getMethod("charValue");
            } else if (primitiveClass == short.class) {
                return Short.class.getMethod("shortValue");
            } else if (primitiveClass == byte.class) {
                return Byte.class.getMethod("byteValue");
            }
        } catch (NoSuchMethodException e) {
            throw new WrappedRunningException(e);
        }
        throw new RunningException("It's not a primitive class: {}", primitiveClass.getName());
    }

    public static Method getPackingMethod(Class<?> primitiveClass) {
        try {
            if (primitiveClass == int.class) {
                return Integer.class.getMethod("valueOf", int.class);
            } else if (primitiveClass == boolean.class) {
                return Boolean.class.getMethod("valueOf", boolean.class);
            } else if (primitiveClass == long.class) {
                return Long.class.getMethod("valueOf", long.class);
            } else if (primitiveClass == double.class) {
                return Double.class.getMethod("valueOf", double.class);
            } else if (primitiveClass == float.class) {
                return Float.class.getMethod("valueOf", float.class);
            } else if (primitiveClass == char.class) {
                return Character.class.getMethod("valueOf", char.class);
            } else if (primitiveClass == short.class) {
                return Short.class.getMethod("valueOf", short.class);
            } else if (primitiveClass == byte.class) {
                return Byte.class.getMethod("valueOf", byte.class);
            }
        } catch (NoSuchMethodException e) {
            throw new WrappedRunningException(e);
        }
        throw new RunningException("It's not a primitive class: {}", primitiveClass.getName());
    }

    public static Class<?> getTypeClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return getTypeClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof TypeVariable) {
            return getTypeClass(((TypeVariable) type).getBounds()[0]);
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[]       bounds       = wildcardType.getLowerBounds();
            if (bounds.length == 0) {
                return getTypeClass(wildcardType.getUpperBounds()[0]);
            }
            return getTypeClass(bounds[0]);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type             componentType    = genericArrayType.getGenericComponentType();
            CharsWriter      cbuf             = new CharsWriter().append('[');
            while (componentType instanceof GenericArrayType) {
                cbuf.write('[');
                componentType = ((GenericArrayType) componentType).getGenericComponentType();
            }
            try {
                return Class.forName(cbuf.append('L')
                        .append(getTypeClass(componentType).getName()).append(';').closeToString());
            } catch (ClassNotFoundException e) {
                throw new WrappedRunningException(e);
            }
        }
        throw new RunningException("Could not convert the type to class: {}.", type);
    }

    public static boolean isEmpty(Object object) {
        return object == null || object.toString().isEmpty();
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isBlank(Object object) {
        if (object == null) {
            return true;
        }
        String value = object.toString();
        for (int i = 0, j = value.length(); i < j; i++) {
            if (value.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(Object object) {
        return !isBlank(object);
    }

    public static String toString(InputStream inputStream) {
        return toString(inputStream, CHARSET_UTF8);
    }

    public static String toString(InputStream inputStream, String charset) {
        return toString(inputStream, Charset.forName(charset));
    }

    public static String toString(InputStream inputStream, Charset charset) {
        return toString(new InputStreamReader(inputStream, charset));
    }

    public static String toString(Reader reader) {
        char[] buffer = mallocBuffer();
        int    size   = 0, len = buffer.length, rlen;
        try {
            while ((rlen = reader.read(buffer, size, len)) != -1) {
                size += rlen;
                if (size == buffer.length) {
                    len = size >> 1;
                    buffer = Arrays.copyOf(buffer, size + len);
                } else {
                    len -= rlen;
                }
            }
            return new String(buffer, 0, size);
        } catch (IOException e) {
            throw new WrappedRunningException(e);
        } finally {
            close(reader);
            recycleBuffer(buffer);
        }
    }

    public static byte[] toBytes(char[] chars) {
        return toBytes(chars, CHARSET_UTF8);
    }

    public static byte[] toBytes(char[] chars, Charset charset) {
        CharBuffer charBuffer = CharBuffer.allocate(chars.length);
        charBuffer.put(chars);
        charBuffer.flip();
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    }

    public static char[] toChars(byte[] bytes) {
        return toChars(bytes, CHARSET_UTF8);
    }

    public static char[] toChars(byte[] bytes, Charset charset) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        CharBuffer charsBuffer = charset.decode(byteBuffer);
        return Arrays.copyOf(charsBuffer.array(), charsBuffer.limit());
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            close(closeable);
        }
    }

    public static <T> T[] growCapacity(T[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static byte[] growCapacity(byte[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static char[] growCapacity(char[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static short[] growCapacity(short[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static int[] growCapacity(int[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static boolean[] growCapacity(boolean[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static float[] growCapacity(float[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static long[] growCapacity(long[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static double[] growCapacity(double[] array, int minCapacity) {
        return Arrays.copyOf(array, newCapacity(array.length, minCapacity));
    }

    public static int newCapacity(int oldCapacity, int minCapacity) {
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        if (newCapacity > MAX_ARRAY_SIZE) {
            newCapacity = (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
        }
        return newCapacity;
    }

    public static long nextIncrement() {
        return ATOMIC_LONG.incrementAndGet();
    }

    public static char[] mallocBuffer() {
        return mallocBuffer(DEFAULT_THREAD_CHAR_BUFFER_SIZE);
    }

    public static char[] mallocBuffer(int minCapacity) {
        SoftReference<char[]> ref = THREAD_CHAR_BUFFER_CACHE.get();
        if (ref != null) {
            char[] buffer = ref.get();
            if (buffer != null && buffer.length >= minCapacity) {
                THREAD_CHAR_BUFFER_CACHE.set(null);
                return buffer;
            }
        }
        return new char[minCapacity];
    }

    public static void recycleBuffer(char[] buffer) {
        if (buffer.length <= MAX_THREAD_REFERENCE_CHAR_BUFFER_SIZE) {
            SoftReference<char[]> oldCache = THREAD_CHAR_BUFFER_CACHE.get();
            if (oldCache != null) {
                char[] oldBuffer = oldCache.get();
                if (oldBuffer != null && oldBuffer.length > buffer.length) {
                    return;
                }
            }
            THREAD_CHAR_BUFFER_CACHE.set(new SoftReference<char[]>(buffer));
        }
    }

    public static int stringSize(int value) {
        if (value < 0) {
            value = -value;
            for (int i = 0; ; i++) {
                if (value <= INT_SIZE_TABLE[i]) {
                    return i + 2;
                }
            }
        }
        for (int i = 0; ; i++) {
            if (value <= INT_SIZE_TABLE[i]) {
                return i + 1;
            }
        }
    }

    public static int stringSize(long value) {
        long p = 10;
        if (value < 0) {
            value = -value;
            for (int i = 1; i < 19; i++) {
                if (value < p) {
                    return i + 1;
                }
                p = 10 * p;
            }
            return 20;
        }
        for (int i = 1; i < 19; i++) {
            if (value < p) {
                return i;
            }
            p = 10 * p;
        }
        return 19;
    }

    public static void getChars(int value, char[] buffer, int charPos) {
        if (value == Integer.MIN_VALUE) {
            int length = MIN_INT_VALUE.length;
            System.arraycopy(MIN_LONG_VALUE, 0, buffer, charPos - length, length);
            return;
        }

        int  q, r;
        char sign = 0;

        if (value < 0) {
            sign = '-';
            value = -value;
        }

        // Generate two DIGIT_CHARS per iteration
        while (value >= 65536) {
            q = value / 100;
            // really: r = value - (q * 100);
            r = value - ((q << 6) + (q << 5) + (q << 2));
            buffer[--charPos] = DIGIT_ONES[r];
            buffer[--charPos] = DIGIT_TENS[r];
            value = q;
        }

        // Fall thru to fast mode for smaller numbers
        // assert(value <= 65536, value);
        for (; ; ) {
            q = (value * 52429) >>> (16 + 3); // q = value/10
            r = value - ((q << 3) + (q << 1)); // r = value-(q*10) ...
            buffer[--charPos] = DIGIT_ONES[r];
            value = q;
            if (value == 0)
                break;
        }

        if (sign != 0) {
            buffer[--charPos] = sign;
        }
    }

    public static void getChars(long value, char[] buffer, int charPos) {
        if (value == Long.MIN_VALUE) {
            int length = MIN_LONG_VALUE.length;
            System.arraycopy(MIN_LONG_VALUE, 0, buffer, charPos - length, length);
            return;
        }

        long q;
        int  r;
        char sign = 0;

        if (value < 0) {
            sign = '-';
            value = -value;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (value > Integer.MAX_VALUE) {
            q = value / 100;
            // really: r = value - (q * 100);
            r = (int) (value - ((q << 6) + (q << 5) + (q << 2)));
            value = q;
            buffer[--charPos] = DIGIT_ONES[r];
            buffer[--charPos] = DIGIT_TENS[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) value;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buffer[--charPos] = DIGIT_ONES[r];
            buffer[--charPos] = DIGIT_TENS[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (; ; ) {
            q2 = (i2 * 52429) >>> (16 + 3);
            r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
            buffer[--charPos] = DIGIT_ONES[r];
            i2 = q2;
            if (i2 == 0)
                break;
        }

        if (sign != 0) {
            buffer[--charPos] = sign;
        }
    }

    public static String loadResourceAsString(String classPath) {
        return loadResourceAsString(classPath, CHARSET_UTF8);
    }

    public static String loadResourceAsString(String classPath, String charset) {
        return loadResourceAsString(classPath, Charset.forName(charset));
    }

    public static String loadResourceAsString(String classPath, Charset charset) {
        InputStream in = loadResourceAsStream(classPath);
        if (in == null) {
            return null;
        }
        try {
            return toString(in, charset);
        } catch (Throwable e) {
            throw new WrappedRunningException(e);
        } finally {
            close(in);
        }
    }

    public static InputStream loadResourceAsStream(String classPath) {
        return ClassTools.INSTANCE.getResourceAsStream(classPath);
    }

    public static Class loadByteCodes(String name, byte[] bytes) {
        return loadByteCodes(name, bytes, 0, bytes.length);
    }

    public static Class loadByteCodes(String className, byte[] bytes, int off, int len) {
        return ClassTools.loadByteCodes(className, bytes, off, len);
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CaptainTools.class.getClassLoader();
        }
        return classLoader;
    }

    public static class ClassTools extends ClassLoader {

        public static final ClassTools INSTANCE = new ClassTools();

        protected ClassTools() {
            super(getClassLoader());
        }

        public static Class loadByteCodes(String className, byte[] bytes, int off, int len) {
            return INSTANCE.defineClass(className, bytes, off, len);
        }

    }

}
