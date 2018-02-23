package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.ClassWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.asm.Type;
import org.jackframework.common.exceptions.RunningException;
import org.jackframework.common.exceptions.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FastField {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FastField.class);

    protected static final Map<Field, FastField> CACHE_MAP = new ConcurrentHashMap<Field, FastField>();

    protected static final String FAST_FIELD_PREFIX =
            FastField.class.getSimpleName() + '$' + System.identityHashCode(FastField.class) + "$";

    protected Field field;

    protected FastField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String toString() {
        return field.toString();
    }

    public abstract Object get(Object target);

    public abstract void set(Object target, Object value);

    public static FastField getFastField(Class<?> clazz, String fieldName) {
        try {
            return getFastField(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new WrappedException(e);
        }
    }

    public static FastField getFastField(Field field) {
        FastField fastConstructor = CACHE_MAP.get(field);
        if (fastConstructor == null) {
            CACHE_MAP.put(field, fastConstructor = createFastField(field));
        }
        return fastConstructor;
    }

    @SuppressWarnings("unchecked")
    public static FastField createFastField(Field field) {
        if (!CaptainTools.isPublic(field)) {
            LOGGER.warn("It's not a completely public field, implements with reflect: {}", field.toGenericString());
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                return new FastField(field) {
                    @Override
                    public Object get(Object target) {
                        try {
                            return field.get(target);
                        } catch (Throwable e) {
                            throw new RunningException(e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                    }
                };
            }
            return new FastField(field) {
                @Override
                public Object get(Object target) {
                    try {
                        return field.get(target);
                    } catch (Throwable e) {
                        throw new RunningException(e);
                    }
                }

                @Override
                public void set(Object target, Object value) {
                    try {
                        field.set(target, value);
                    } catch (Throwable e) {
                        throw new RunningException(e);
                    }
                }
            };
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = FAST_FIELD_PREFIX + CaptainTools.nextIncrement();
        String superClassName = Type.getInternalName(FastField.class);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, superClassName, null);

        String constructorDesc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Field.class));
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", constructorDesc, false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();

        Type objectType = Type.getType(Object.class);

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", Type.getMethodDescriptor(objectType, objectType), null, null);

        String fieldClassName = Type.getInternalName(field.getDeclaringClass());
        Class<?> fieldType = field.getType();
        int modifiers = field.getModifiers();

        boolean isStatic = Modifier.isStatic(modifiers);

        if (!isStatic) {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, fieldClassName);
        }

        String fieldName = field.getName();
        String fieldTypeDesc = Type.getDescriptor(fieldType);

        mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, fieldClassName, fieldName, fieldTypeDesc);

        if (fieldType.isPrimitive()) {
            Class<?> packingClass = CaptainTools.getPackingClass(fieldType);
            Method packMethod = CaptainTools.getPackingMethod(fieldType);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(packingClass),
                               packMethod.getName(), Type.getMethodDescriptor(packMethod), false);
        }

        mv.visitInsn(Opcodes.ARETURN);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set",
                            Type.getMethodDescriptor(Type.VOID_TYPE, objectType, objectType), null, null);

        if (Modifier.isFinal(modifiers)) {
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 3);
            mv.visitEnd();
        } else {
            if (!isStatic) {
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitTypeInsn(Opcodes.CHECKCAST, fieldClassName);
            }

            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, fieldTypeDesc);

            if (fieldType.isPrimitive()) {
                Class<?> packingClass = CaptainTools.getPackingClass(fieldType);
                Method unpackMethod = CaptainTools.getUnpackingMethod(fieldType);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(packingClass),
                                   unpackMethod.getName(), Type.getMethodDescriptor(unpackMethod), false);
            }

            mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD,
                              fieldClassName, fieldName, fieldTypeDesc);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitEnd();
        }

        try {
            return (FastField) CaptainTools
                    .loadByteCodes(className, cw.toByteArray())
                    .getConstructor(Field.class)
                    .newInstance(field);
        } catch (Throwable e) {
            throw new WrappedException(e);
        }
    }


}
