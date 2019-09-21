package io.github.zekerzhayard.vanillarecover.asm.transformers.itf;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractInsnTransformer {
    public abstract boolean isTargetInsn(AbstractInsnNode ain);

    public abstract void transform(MethodNode mn, AbstractInsnNode ain);
}
