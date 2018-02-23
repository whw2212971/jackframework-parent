package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.ClassWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.asm.Type;
import org.jackframework.common.exceptions.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FastMethod {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FastMethod.class);

    protected static final Map<Method, FastMethod> CACHE_MAP = new ConcurrentHashMap<Method, FastMethod>();

    protected static final String FAST_METHOD_PREFIX =
            FastMethod.class.getSimpleName() + '$' + System.identityHashCode(FastMethod.class) + "$";

    protected Method method;

    protected FastMethod(Method method) {
        this.method = method;
    }

    public abstract Object invoke(Object invoker, Object... args);

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return method.toString();
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof FastMethod && method.equals(((FastMethod) obj).method);
    }

    public static FastMethod getFastMethod(Class<?> type, String methodName, Class<?>... paramTypes) {
        try {
            return getFastMethod(type.getDeclaredMethod(methodName, paramTypes));
        } catch (NoSuchMethodException e) {
            throw new WrappedException(e);
        }
    }

    public static FastMethod getFastMethod(Method method) {
        FastMethod fastMethod = CACHE_MAP.get(method);
        if (fastMethod == null) {
            CACHE_MAP.put(method, fastMethod = createFastMethod(method));
        }
        return fastMethod;
    }

    @SuppressWarnings("unchecked")
    protected static FastMethod createFastMethod(Method method) {
        if (!CaptainTools.isPublic(method)) {
            LOGGER.warn("It's not a completely public method, implements with reflect: {}", method.toGenericString());
            method.setAccessible(true);
            return new FastMethod(method) {
                @Override
                public Object invoke(Object invoker, Object... args) {
                    try {
                        return method.invoke(invoker, args);
                    } catch (Throwable e) {
                        throw new WrappedException(e);
                    }
                }
            };
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className = FAST_METHOD_PREFIX + CaptainTools.nextIncrement();
        String superClassName = Type.getInternalName(FastMethod.class);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, superClassName, null);

        // public FastMethod$identifier(Mthod method) {
        String constructorDesc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Method.class));

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null);

        // super(method);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", constructorDesc, false);
        mv.visitInsn(Opcodes.RETURN);

        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        Type objectType = Type.getType(Object.class);

        // public Object invoke(Object target, Object[] args) {
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke",
                            Type.getMethodDescriptor(objectType, objectType, Type.getType(Object[].class)), null, null);

        boolean isStatic = Modifier.isStatic(method.getModifiers());

        String methodClassName = Type.getInternalName(method.getDeclaringClass());

        // static -> return TargetClass.invoke((TargetParamTypes) args);
        // not static ->
        // return ((TargetClass) target).invoke((TargetParamTypes) args);
        if (!isStatic) {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, methodClassName);
        }

        AsmTools.visitArguments(mv, method.getParameterTypes(), 2);
        mv.visitMethodInsn(isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL,
                           methodClassName, method.getName(), Type.getMethodDescriptor(method), false);

        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Void.TYPE)) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else if (returnType.isPrimitive()) {
            Class<?> packingClass = CaptainTools.getPackingClass(returnType);
            Method packingMethod = CaptainTools.getPackingMethod(returnType);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(packingClass),
                               packingMethod.getName(), Type.getMethodDescriptor(packingMethod), false);
        }

        mv.visitInsn(Opcodes.ARETURN);

        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();

        try {
            return (FastMethod) CaptainTools
                    .loadByteCodes(className, cw.toByteArray())
                    .getConstructor(Method.class).newInstance(method);
        } catch (Throwable e) {
            throw new WrappedException(e);
        }
    }

}
