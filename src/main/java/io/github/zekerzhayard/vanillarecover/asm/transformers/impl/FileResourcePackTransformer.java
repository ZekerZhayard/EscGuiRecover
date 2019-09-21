package io.github.zekerzhayard.vanillarecover.asm.transformers.impl;

import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractClassTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractInsnTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FileResourcePackTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.resources.FileResourcePack");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return (methodName.equals("func_110591_a") || methodName.equals("getInputStreamByName")) && methodDesc.equals("(Ljava/lang/String;)Ljava/io/InputStream;");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[0];
                }

                @Override
                public void transform(MethodNode mn) {
                    InsnList il = new InsnList();
                    LabelNode ln = new LabelNode();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/FileResourcePackHook", "getInputStreamByName", "(Ljava/lang/String;)Ljava/io/InputStream;", false));
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new JumpInsnNode(Opcodes.IFNULL, ln));
                    il.add(new InsnNode(Opcodes.ARETURN));
                    il.add(ln);
                    il.add(new InsnNode(Opcodes.POP));
                    mn.instructions.insert(il);
                }
            },
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return (methodName.equals("func_110593_b") || methodName.equals("hasResourceName")) && methodDesc.equals("(Ljava/lang/String;)Z");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[0];
                }

                @Override
                public void transform(MethodNode mn) {
                    InsnList il = new InsnList();
                    LabelNode ln = new LabelNode();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/FileResourcePackHook", "getInputStreamByName", "(Ljava/lang/String;)Ljava/io/InputStream;", false));
                    il.add(new JumpInsnNode(Opcodes.IFNULL, ln));
                    il.add(new InsnNode(Opcodes.ICONST_1));
                    il.add(new InsnNode(Opcodes.IRETURN));
                    il.add(ln);
                    mn.instructions.insert(il);
                }
            }
        };
    }
}
