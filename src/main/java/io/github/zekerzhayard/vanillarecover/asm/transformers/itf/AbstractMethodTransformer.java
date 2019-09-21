package io.github.zekerzhayard.vanillarecover.asm.transformers.itf;

import io.github.zekerzhayard.vanillarecover.asm.transformers.ClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractMethodTransformer {
    private AbstractInsnTransformer[] insnTransformers = this.getInsnTransformers();

    public abstract boolean isTargetMethod(String methodName, String methodDesc);

    public abstract AbstractInsnTransformer[] getInsnTransformers();

    public void transform(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            for (AbstractInsnTransformer ait : this.insnTransformers) {
                if (ait.isTargetInsn(ain)) {
                    ClassTransformer.LOGGER.info("Found the node: " + ain.getOpcode() + " " + ain.getType());
                    ait.transform(mn, ain);
                }
            }
        }
    }
}
