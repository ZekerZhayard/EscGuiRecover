package io.github.zekerzhayard.vanillarecover.asm.transformers.impl;

import io.github.zekerzhayard.vanillarecover.asm.VanillaRecoverTweaker;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractClassTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractInsnTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FMLSanityCheckerTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals(VanillaRecoverTweaker.getFmlPrefix() + "common.asm.FMLSanityChecker");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return methodName.equals("injectData") && methodDesc.equals("(Ljava/util/Map;)V");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals(VanillaRecoverTweaker.getFmlPrefix().replace('.', '/') + "common/patcher/ClassPatchManager") && min.name.equals("setup") && min.desc.equals("(L" + VanillaRecoverTweaker.getFmlPrefix().replace('.', '/') + "relauncher/Side;)V");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/FMLSanityCheckerHook", ((MethodInsnNode) ain).name, "(Ljava/lang/Object;Ljava/lang/Object;)V", false));
                            }
                        }
                    };
                }
            }
        };
    }
}
