package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.CharsWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.exceptions.RunningException;

import java.lang.reflect.*;

import static org.jackframework.common.asm.Type.*;

public abstract class AsmTools {

    public static void visitArguments(MethodVisitor mv, Class<?>[] paramTypes, int localIndex) {
        for (int i = 0, j = paramTypes.length; i < j; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, localIndex);
            mv.visitLdcInsn(i);
            mv.visitInsn(Opcodes.AALOAD);
            Class<?> parameterType = paramTypes[i];
            if (parameterType.isPrimitive()) {
                Class<?> packingClass = CaptainTools.getPackingClass(parameterType);
                Method unpackingMethod = CaptainTools.getUnpackingMethod(parameterType);
                String packingClassName = getInternalName(packingClass);
                mv.visitTypeInsn(Opcodes.CHECKCAST, packingClassName);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                   packingClassName, unpackingMethod.getName(), getMethodDescriptor(unpackingMethod), false);
            } else {
                mv.visitTypeInsn(Opcodes.CHECKCAST, getInternalName(parameterType));
            }
        }
    }

    public static String getSignature(Type type) {
        CharsWriter writer = new CharsWriter();
        writeSignature(writer, type, true);
        return writer.closeToString();
    }

    protected static void writeSignature(CharsWriter writer, Type type, boolean writeEnd) {
        if (type instanceof Class) {
            String desc = getDescriptor((Class<?>) type);
            if (!writeEnd && desc.endsWith(";")) {
                desc = desc.substring(0, desc.length() - 1);
            }
            writer.append(desc).closeToString();
            return;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            writeSignature(writer, paramType.getRawType(), false);
            writer.write('<');
            for (Type actualType : paramType.getActualTypeArguments()) {
                writeSignature(writer, actualType, true);
            }
            writer.write(">;");
            return;
        }
        if (type instanceof TypeVariable) {
            writeSignature(writer, ((TypeVariable) type).getBounds()[0], true);
            return;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] bounds = wildcardType.getLowerBounds();
            if (bounds.length == 0) {
                writeSignature(writer, wildcardType.getUpperBounds()[0], true);
                return;
            }
            writeSignature(writer, bounds[0], true);
            return;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            writer.write('[');
            while (componentType instanceof GenericArrayType) {
                writer.write('[');
                componentType = ((GenericArrayType) componentType).getGenericComponentType();
            }
            writeSignature(writer, componentType, true);
            return;
        }
        throw new RunningException("Unknow generic type: {}", type);
    }

}
