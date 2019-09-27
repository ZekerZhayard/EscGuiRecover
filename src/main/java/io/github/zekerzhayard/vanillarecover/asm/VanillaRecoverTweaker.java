package io.github.zekerzhayard.vanillarecover.asm;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaRecoverTweaker implements ITweaker {
    public static String mcVersion;

    private final static Logger LOGGER = LogManager.getLogger();
    private final static String TRANSFORMER = "io.github.zekerzhayard.vanillarecover.asm.transformers.ClassTransformer";
    private static Object deobfInstance;
    private static Method mapClassName;
    private static Method mapMethodName;
    private static Method mapMethodDesc;

    static {
        VanillaRecoverTweaker.clinit();
    }

    public static String getFmlPrefix() {
        if (mcVersion == null) {
            throw new RuntimeException("Couldn't catch Minecraft version!");
        }
        return mcVersion.equals("1.7.10") ? "cpw.mods.fml." : "net.minecraftforge.fml.";
    }

    public static String mapClassName(String className) {
        if (deobfInstance != null && mapClassName != null) {
            try {
                return (String) mapClassName.invoke(deobfInstance, className);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return className;
        }
        try {
            Class<?> deobf = Launch.classLoader.loadClass(getFmlPrefix() + "common.asm.transformers.deobf.FMLDeobfuscatingRemapper");
            deobfInstance = deobf.getField("INSTANCE").get(null);
            mapClassName = deobf.getMethod("map", String.class);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            return className;
        }
        return mapClassName(className);
    }

    public static String mapMethodName(String className, String methodName, String methodDesc) {
        if (deobfInstance != null && mapMethodName != null) {
            try {
                return (String) mapMethodName.invoke(deobfInstance, className, methodName, methodDesc);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return methodName;
        }
        try {
            Class<?> deobf = Launch.classLoader.loadClass(getFmlPrefix() + "common.asm.transformers.deobf.FMLDeobfuscatingRemapper");
            deobfInstance = deobf.getField("INSTANCE").get(null);
            mapMethodName = deobf.getMethod("mapMethodName", String.class, String.class, String.class);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            return methodName;
        }
        return mapMethodName(className, methodName, methodDesc);
    }

    public static String mapMethodDesc(String methodDesc) {
        if (deobfInstance != null && mapMethodDesc != null) {
            try {
                return (String) mapMethodDesc.invoke(deobfInstance, methodDesc);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return methodDesc;
        }
        try {
            Class<?> deobf = Launch.classLoader.loadClass(getFmlPrefix() + "common.asm.transformers.deobf.FMLDeobfuscatingRemapper");
            deobfInstance = deobf.getField("INSTANCE").get(null);
            mapMethodDesc = deobf.getMethod("mapMethodDesc", String.class);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            return methodDesc;
        }
        return mapMethodDesc(methodDesc);
    }

    @SuppressWarnings("unchecked")
    private static void clinit() {
        try {
            mcVersion = (String) FieldUtils.readStaticField(Launch.classLoader.getClass().getClassLoader().loadClass("net.minecraftforge.common.MinecraftForge"), "MC_VERSION");
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (mcVersion == null) {
            LOGGER.warn("Counld not get Minecraft version!");
            return;
        }

        Class<?> class_CoreModManager = null;
        try {
            class_CoreModManager = Class.forName(getFmlPrefix() + "relauncher.CoreModManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (class_CoreModManager == null) {
            LOGGER.warn("Counld not catch CoreModManager!");
            return;
        }

        List<String> candidateModFiles = null, ignoredModFiles = null;
        for (Field field : class_CoreModManager.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getName().equals("reparsedCoremods") || field.getName().equals("candidateModFiles")) {
                    candidateModFiles = (List<String>) field.get(null);
                } else if (field.getName().equals("loadedCoremods") || field.getName().equals("ignoredModFiles")) {
                    ignoredModFiles = (List<String>) field.get(null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (candidateModFiles == null || ignoredModFiles == null) {
            LOGGER.warn("Counld not catch candidateModFiles or ignoredModFiles!");
            return;
        }

        String[] modNames = new File(Launch.minecraftHome, "mods").list();
        if (modNames == null) {
            LOGGER.warn("Counld not get mods directory!");
            return;
        }
        for (String modName : modNames) {
            if (modName.endsWith("@3@0.jar")) {
                LOGGER.info("Catch a Netease mod! (" + modName + ")");
                candidateModFiles.remove(modName);
                ignoredModFiles.add(modName);
            }
        }

        String fmlPluginWrapper = getFmlPrefix() + "relauncher.CoreModManager$FMLPluginWrapper";
        for (ITweaker tweaker : (List<ITweaker>) Launch.blackboard.get("Tweaks")) {
            if (tweaker.getClass().getName().equals(fmlPluginWrapper)) {
                try {
                    Field field = tweaker.getClass().getField("coreModInstance");
                    field.setAccessible(true);
                    Class<?> clazz = field.get(tweaker).getClass();
                    if (clazz.getName().startsWith("com.netease.mc.") && clazz.getProtectionDomain().getCodeSource().getLocation().getFile().endsWith("@3@0.jar")) {
                        LOGGER.info("Catch a Netease core mod! (" + clazz.getName() + ")");
                        field.set(tweaker, new DummyFMLLoadingPlugin());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        LOGGER.info("CandidateModFiles: " + candidateModFiles);
        LOGGER.info("IgnoredModFiles: " + ignoredModFiles);

        if (mcVersion.equals("1.12.2")) {
            LOGGER.info("Minecraft version is 1.12.2!");
            return;
        }

        Launch.classLoader.registerTransformer(TRANSFORMER);
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        try {
            List<IClassTransformer> transformers = (List<IClassTransformer>) FieldUtils.readDeclaredField(classLoader, "transformers", true);
            IClassTransformer transformer = null;
            for (int i = transformers.size() - 1; i >=0; i--) {
                transformer = transformers.get(i);
                if (transformer.getClass().getName().equals(TRANSFORMER)) {
                    transformers.remove(i);
                    break;
                }
                transformer = null;
            }
            if (transformer == null) {
                classLoader.registerTransformer(TRANSFORMER);
            } else {
                transformers.add(transformer);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
