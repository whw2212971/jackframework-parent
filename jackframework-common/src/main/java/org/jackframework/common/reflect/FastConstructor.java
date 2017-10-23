package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.ClassWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.asm.Type;
import org.jackframework.common.exceptions.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public abstract class FastConstructor<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FastConstructor.class);

    protected static final Map<Constructor, FastConstructor> CACHE_MAP =
            new ConcurrentHashMap<Constructor, FastConstructor>();

    protected static final String FAST_CONSTRUCTOR_PREFIX =
            FastConstructor.class.getSimpleName() + '$' + System.identityHashCode(FastConstructor.class) + "$";

    protected Constructor<T> constructor;

    public FastConstructor(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    public abstract T newInstance(Object... args);

    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public String toString() {
        return constructor.toString();
    }

    @Override
    public int hashCode() {
        return constructor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                obj instanceof FastConstructor &&
                constructor.equals(((FastConstructor) obj).constructor);
    }

    public static <T> FastConstructor<T> getFastConstructor(Class<T> type, Class<?>... paramTypes) {
        try {
            return getFastConstructor(type.getConstructor(paramTypes));
        } catch (NoSuchMethodException e) {
            throw new WrappedException(e);
        }
    }

    public static <T> FastConstructor<T> getFastConstructor(Constructor<T> constructor) {
        FastConstructor fastConstructor = CACHE_MAP.get(constructor);
        if (fastConstructor == null) {
            CACHE_MAP.put(constructor, fastConstructor = createFastConstructor(constructor));
        }
        return fastConstructor;
    }

    protected static <T> FastConstructor<T> createFastConstructor(Constructor<T> constructor) {
        if (!CaptainTools.isPublic(constructor)) {
            LOGGER.warn(
                    "It's not a completely public constructor, implements with reflect: {}",
                    constructor.toGenericString());
            return new FastConstructor<T>(constructor) {
                @Override
                public T newInstance(Object... args) {
                    try {
                        return constructor.newInstance(args);
                    } catch (Throwable e) {
                        throw new WrappedException(e);
                    }
                }
            };
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className = FAST_CONSTRUCTOR_PREFIX + CaptainTools.nextIncrement();

        String superClassName = Type.getInternalName(FastConstructor.class);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, superClassName, null);

        // public FastConstructor$identifier(Constructor constructor) {
        String constructorDesc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Constructor.class));

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null);

        // super(constructor);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", constructorDesc, false);
        mv.visitInsn(Opcodes.RETURN);

        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // public Object newInstance(Object[] args) {
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "newInstance",
                Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object[].class)), null, null);

        String constructorClassName = Type.getInternalName(constructor.getDeclaringClass());

        // return new ClassName(args);
        mv.visitTypeInsn(Opcodes.NEW, constructorClassName);
        mv.visitInsn(Opcodes.DUP);

        AsmTools.visitArguments(mv, constructor.getParameterTypes(), 1);

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                constructorClassName, "<init>", Type.getConstructorDescriptor(constructor), false);

        mv.visitInsn(Opcodes.ARETURN);
        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        try {
            return (FastConstructor<T>) CaptainTools
                    .loadByteCodes(className, cw.toByteArray())
                    .getConstructor(Constructor.class).newInstance(constructor);
        } catch (Throwable e) {
            throw new WrappedException(e);
        }
    }


}
