package io.github.zekerzhayard.vanillarecover.asm.transformers.impl;

import io.github.zekerzhayard.vanillarecover.asm.VanillaRecoverTweaker;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractClassTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractInsnTransformer;
import io.github.zekerzhayard.vanillarecover.asm.transformers.itf.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class DefaultResourcePackTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.resources.DefaultResourcePack");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return (methodName.equals("func_152780_c") || methodName.equals("getInputStreamAssets")) && methodDesc.equals("(Lnet/minecraft/util/ResourceLocation;)Ljava/io/InputStream;");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[0];
                }

                @Override
                public void transform(MethodNode mn) {
                    InsnList il = new InsnList();
                    LabelNode ln = new LabelNode();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/DefaultResourcePackHook", "getInputStreamAssets", "(Lnet/minecraft/client/resources/DefaultResourcePack;Lnet/minecraft/util/ResourceLocation;)Ljava/io/InputStream;", false));
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
                    return (methodName.equals("func_110605_c") || methodName.equals("getResourceStream")) && methodDesc.equals("(Lnet/minecraft/util/ResourceLocation;)Ljava/io/InputStream;");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("java/lang/Class") && min.name.equals("getResourceAsStream") && min.desc.equals("(Ljava/lang/String;)Ljava/io/InputStream;");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/DefaultResourcePackHook", "getResourceAsStream", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;", false));
                            }
                        }
                    };
                }
            },
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return (methodName.equals("func_135058_a") || methodName.equals("getPackMetadata")) && (methodDesc.equals("(Lnet/minecraft/client/resources/data/IMetadataSerializer;Ljava/lang/String;)Lnet/minecraft/client/resources/data/IMetadataSection;") || methodDesc.equals("(Lnet/minecraft/client/resources/data/MetadataSerializer;Ljava/lang/String;)Lnet/minecraft/client/resources/data/IMetadataSection;"));
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    String className = VanillaRecoverTweaker.mapClassName(min.owner);
                                    String methodName = VanillaRecoverTweaker.mapMethodName(min.owner, min.name, min.desc);
                                    String methodDesc = VanillaRecoverTweaker.mapMethodDesc(min.desc);
                                    return className.equals("net/minecraft/client/resources/AbstractResourcePack") && (methodName.equals("func_110596_a") || methodName.equals("readMetadata")) && (methodDesc.equals("(Lnet/minecraft/client/resources/data/MetadataSerializer;Ljava/io/InputStream;Ljava/lang/String;)Lnet/minecraft/client/resources/data/IMetadataSection;") || methodDesc.equals("(Lnet/minecraft/client/resources/data/IMetadataSerializer;Ljava/io/InputStream;Ljava/lang/String;)Lnet/minecraft/client/resources/data/IMetadataSection;"));
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insertBefore(ain.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/vanillarecover/hooks/DefaultResourcePackHook", "readMetadata", "(Ljava/io/InputStream;)Ljava/io/InputStream;", false));
                            }
                        }
                    };
                }
            }
        };
    }
}
