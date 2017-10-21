package org.jackframework.service.converter;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.asm.ClassWriter;
import org.jackframework.common.asm.MethodVisitor;
import org.jackframework.common.asm.Opcodes;
import org.jackframework.common.exceptions.RunningException;
import org.jackframework.common.exceptions.WrappedRunningException;
import org.jackframework.common.reflect.AsmTools;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.jackframework.common.asm.Type.*;

public abstract class AsmParamBean {

    protected static final String BEAN_CLASS_PREFIX =
            AsmParamBean.class.getSimpleName() + '$' + System.identityHashCode(AsmParamBean.class) + "$";

    public abstract Object[] getArguments();

    @SuppressWarnings("unchecked")
    public static AsmParamBean createAsmParamBean(Method method) {
        Type[]   paramTypes = method.getGenericExceptionTypes();
        String[] paramNames = ConverterUtils.getParameterNames(method);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className      = BEAN_CLASS_PREFIX + CaptainTools.nextIncrement();
        String superClassName = getInternalName(AsmParamBean.class);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, superClassName, null);

        // public AsmParamBean$identifier() {
        String        constructorDesc = getMethodDescriptor(VOID_TYPE);
        MethodVisitor mv              = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null);
        // super();
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", constructorDesc, false);
        mv.visitInsn(Opcodes.RETURN);
        // }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // public Object[] getArguments() {
        mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC, "getArguments", getMethodDescriptor(getType(Object[].class)), null, null);

        int paramSize = paramTypes.length;

        // Object[] result = new Object[paramSize];
        iConst(mv, paramSize);
        mv.visitInsn(Opcodes.ANEWARRAY);
        mv.visitVarInsn(Opcodes.ASTORE, 1);

        for (int i = 0; i < paramSize; i++) {
            Type   paramType = paramTypes[i];
            String paramName = paramNames[i];

            if (!CaptainTools.isPublic(paramType)) {
                throw new RunningException(
                        "The parameter {} of method {} is not public.", paramName, method.toGenericString());
            }

            String fieldDesc = getDescriptor(CaptainTools.getTypeClass(paramType));

            cw.visitField(Opcodes.ACC_PUBLIC, paramName, fieldDesc, AsmTools.getSignature(paramType), null);

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
            return (AsmParamBean)
                    CaptainTools.loadByteCodes(className, cw.toByteArray()).getConstructor().newInstance();
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
