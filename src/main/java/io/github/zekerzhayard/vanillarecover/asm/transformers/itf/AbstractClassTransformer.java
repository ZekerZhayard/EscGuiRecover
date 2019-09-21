package io.github.zekerzhayard.vanillarecover.asm.transformers.itf;

import io.github.zekerzhayard.vanillarecover.asm.VanillaRecoverTweaker;
import io.github.zekerzhayard.vanillarecover.asm.transformers.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractClassTransformer {
    private AbstractMethodTransformer[] methodTransformers = this.getMethodTransformers();

    public abstract boolean isTargetClass(String className);

    public abstract AbstractMethodTransformer[] getMethodTransformers();

    public void transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            for (AbstractMethodTransformer amt : this.methodTransformers) {
                String mappedMethodName = VanillaRecoverTweaker.mapMethodName(cn.name, mn.name, mn.desc);
                String mappedMathodDesc = VanillaRecoverTweaker.mapMethodDesc(mn.desc);
                if (amt.isTargetMethod(mappedMethodName, mappedMathodDesc)) {
                    ClassTransformer.LOGGER.info(String.format("Found the method: %s%s -> %s%s", mn.name, mn.desc, mappedMethodName, mappedMathodDesc));
                    amt.transform(mn);
                }
            }
        }
    }
}
