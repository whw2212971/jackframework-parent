package org.jackframework.common.reflect;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.asm.Type;
import org.jackframework.common.asm.signature.SignatureWriter;

import java.lang.reflect.Method;

public abstract class AsmTools {

    public static void visitArguments(MethodVisitor mv, Class<?>[] paramTypes, int localIndex) {
        for (int i = 0, j = paramTypes.length; i < j; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, localIndex);
            mv.visitLdcInsn(i);
            mv.visitInsn(Opcodes.AALOAD);
            Class<?> parameterType = paramTypes[i];
            if (parameterType.isPrimitive()) {
                Class<?> packingClass     = CaptainTools.getPackingClass(parameterType);
                Method   unpackingMethod  = CaptainTools.getUnpackingMethod(parameterType);
                String   packingClassName = Type.getInternalName(packingClass);
                mv.visitTypeInsn(Opcodes.CHECKCAST, packingClassName);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        packingClassName, unpackingMethod.getName(), Type.getMethodDescriptor(unpackingMethod), false);
            } else {
                mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(parameterType));
            }
        }
    }

    public static String getSignature(Type type) {
        return getSignature(new SignatureWriter(), type);
    }

    protected static String getSignature(SignatureWriter writer, Type type) {
        return null;
    }

}
