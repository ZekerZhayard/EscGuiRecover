package io.github.zekerzhayard.vanillarecover.asm.transformers.impl;

import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractClassTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractInsnTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MainTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.main.Main");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return methodName.equals("main") && methodDesc.equals("([Ljava/lang/String;)V");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[0];
                }

                @Override
                public void transform(MethodNode mn) {
                    InsnList il = new InsnList();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/MainHook", "main", "([Ljava/lang/String;)[Ljava/lang/String;", false));
                    il.add(new VarInsnNode(Opcodes.ASTORE, 0));
                    mn.instructions.insert(il);
                }
            }
        };
    }
}
