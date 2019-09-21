package io.github.zekerzhayard.vanillarecover.hooks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.regex.Pattern;

import LZMA.LzmaInputStream;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import io.github.zekerzhayard.vanillarecover.asm.VanillaRecoverTweaker;
import net.minecraft.launchwrapper.Launch;

public class FMLSanityCheckerHook {
    @SuppressWarnings("unchecked")
    public static void setup(Object instance_ClassPatchManager, Object side) {
        JarInputStream jis;
        try {
            InputStream binpatchesCompressed = FMLSanityCheckerHook.class.getResourceAsStream("/binpatches/" + VanillaRecoverTweaker.mcVersion + "/binpatches.pack.lzma");
            if (binpatchesCompressed == null) {
                System.out.println("Couldn't get binpatches!");
                return;
            }
            ByteArrayOutputStream jarBytes = new ByteArrayOutputStream();
            Pack200.newUnpacker().unpack(new LzmaInputStream(binpatchesCompressed), new JarOutputStream(jarBytes));
            jis = new JarInputStream(new ByteArrayInputStream(jarBytes.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred reading binary patches. Expect severe problems!", e);
        }

        Field field_sourceClassName = null;
        Field field_patches = null;
        Field field_patchedClasses = null;
        Method method_readPatch = null;
        try {
            field_sourceClassName = Launch.classLoader.loadClass(VanillaRecoverTweaker.getFmlPrefix() + "common.patcher.ClassPatch").getField("sourceClassName");
            field_patches = instance_ClassPatchManager.getClass().getDeclaredField("patches");
            field_patches.setAccessible(true);
            field_patchedClasses = instance_ClassPatchManager.getClass().getDeclaredField("patchedClasses");
            field_patchedClasses.setAccessible(true);
            method_readPatch = instance_ClassPatchManager.getClass().getDeclaredMethod("readPatch", JarEntry.class, JarInputStream.class);
            method_readPatch.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (method_readPatch == null) {
            System.out.println("Couldn't get ClassPatch member!");
            return;
        }

        ListMultimap<String, Object> patches = ArrayListMultimap.create();
        Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/.*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        do {
            try {
                JarEntry entry = jis.getNextJarEntry();
                if (entry == null) {
                    break;
                }
                if (binpatchMatcher.matcher(entry.getName()).matches()) {
                    Object classPatch = method_readPatch.invoke(instance_ClassPatchManager, entry, jis);
                    if (classPatch != null) {
                        patches.put((String) field_sourceClassName.get(classPatch), classPatch);
                    }
                } else {
                    jis.closeEntry();
                }
            } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } while (true);
        System.out.println("Read " + patches.size() + " binary patches");

        try {
            field_patches.set(instance_ClassPatchManager, patches);
            ((Map<String, byte[]>) field_patchedClasses.get(instance_ClassPatchManager)).clear();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
