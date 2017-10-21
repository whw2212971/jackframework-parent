package org.jackframework.service.converter;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.ClassWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.exceptions.RunningException;
import org.jackframework.common.exceptions.WrappedRunningException;
import org.jackframework.common.reflect.AsmTools;
import org.jackframework.service.component.ServiceMethodHandler;

import java.lang.reflect.Type;

import static org.jackframework.common.asm.Type.*;

public abstract class FastArgumentsBean {

    protected static final String BEAN_CLASS_PREFIX =
            FastArgumentsBean.class.getSimpleName() + '$' + System.identityHashCode(FastArgumentsBean.class) + "$";

    public abstract Object[] getArguments();

    @SuppressWarnings("unchecked")
    public static Class<? extends FastArgumentsBean> createFastArgumentsBeanClass(ServiceMethodHandler handler) {
        Type[]   paramTypes = handler.getGenericParameterTypes();
        String[] paramNames = handler.getParameterNames();

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className      = BEAN_CLASS_PREFIX + CaptainTools.nextIncrement();
        String superClassName = getInternalName(FastArgumentsBean.class);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, superClassName, null);

        // public FastArgumentsBean$identifier() {
        String        constructorDesc = getMethodDescriptor(VOID_TYPE);
        MethodVisitor mv              = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null);
        // super();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", constructorDesc, false);
        mv.visitInsn(Opcodes.RETURN);
        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // public Object[] getArguments() {
        mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC, "getArguments", getMethodDescriptor(getType(Object[].class)), null, null);

        int paramCount = paramTypes.length;

        // Object[] result = new Object[paramSize];
        iConst(mv, paramCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, getInternalName(Object.class));
        mv.visitVarInsn(Opcodes.ASTORE, 1);

        for (int i = 0; i < paramCount; i++) {
            Type   paramType = paramTypes[i];
            String paramName = paramNames[i];

            if (!CaptainTools.isPublic(paramType)) {
                throw new RunningException(
                        "The parameter '{}' of method '{}' is not public.",
                        paramName, handler.getServiceMethod().getMethod().toGenericString());
            }

            Class<?> paramClass = CaptainTools.getTypeClass(paramType);
            if (paramClass.isPrimitive()) {
                paramClass = CaptainTools.getPackingClass(paramClass);
            }

            String fieldDesc = getDescriptor(paramClass);

            cw.visitField(Opcodes.ACC_PUBLIC, paramName, fieldDesc,
                    paramType instanceof Class ? null : AsmTools.getSignature(paramType), null);

            mv.visitVarInsn(Opcodes.ALOAD, 1);
            iConst(mv, i);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, className, paramName, fieldDesc);
            mv.visitInsn(Opcodes.AASTORE);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        try {
            return (Class<? extends FastArgumentsBean>) CaptainTools.loadByteCodes(className, cw.toByteArray());
        } catch (Throwable e) {
            throw new WrappedRunningException(e);
        }
    }

    protected static void iConst(MethodVisitor mv, int iConst) {
        if (iConst <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + iConst);
        } else {
            mv.visitVarInsn(Opcodes.BIPUSH, iConst);
        }
    }


}
