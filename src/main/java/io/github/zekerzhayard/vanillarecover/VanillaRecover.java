package io.github.zekerzhayard.vanillarecover;

import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.reflect.FieldUtils;

public class VanillaRecover {
    private static String mcVersion = VanillaRecover._getMcVersion();
    private static String mcMajorVersion = VanillaRecover._getMajorVersion();

    public static String getMcVersion() {
        return VanillaRecover.mcVersion;
    }

    public static String getMcMajorVersion() {
        return VanillaRecover.mcMajorVersion;
    }

    private static String _getMcVersion() {
        try {
            return (String) FieldUtils.readStaticField(MinecraftForge.class, "MC_VERSION");
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String _getMajorVersion() {
        try {
            String[] versions = VanillaRecover.mcVersion.split("\\.");
            return versions[0] + "." + versions[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return "";
    }
}
