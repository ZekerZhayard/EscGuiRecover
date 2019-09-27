package io.github.zekerzhayard.vanillarecover.asm.transformers;

import java.util.ServiceLoader;

import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassTransformer implements IClassTransformer {
    public final static Logger LOGGER = LogManager.getLogger("VanillaRecover");
    private ServiceLoader<AbstractClassTransformer> sl = ServiceLoader.load(AbstractClassTransformer.class, Launch.classLoader);

    @Override
    public byte[] transform(String className, String transformedName, byte[] basicClass) {
        for (AbstractClassTransformer act : this.sl) {
            if (act.isTargetClass(transformedName)) {
                LOGGER.info(String.format("Found the class: %s -> %s", className, transformedName));
                ClassNode cn = new ClassNode();
                new ClassReader(basicClass).accept(cn, ClassReader.SKIP_FRAMES);
                act.transform(cn);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                cn.accept(cw);
                return cw.toByteArray();
            }
        }
        return basicClass;
    }
}
