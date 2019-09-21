package io.github.zekerzhayard.vanillarecover.hooks;

import java.io.InputStream;

import io.github.zekerzhayard.vanillarecover.VanillaRecover;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class DefaultResourcePackHook {
    public static InputStream getInputStreamAssets(DefaultResourcePack pack, ResourceLocation location) throws IllegalAccessException {
        if (location.getPath().startsWith("lang/")) {
            InputStream is = pack.getResourceStream(location);
            System.out.println("VanillaRecoverPack - getInputStreamAssets - " + is);
            return is;//pack.getResourceStream(location);
        }
        return null;
    }

    public static InputStream getResourceAsStream(Class<?> clazz, String name) {
        InputStream inputStream = null;
        if (name.equals("/assets/minecraft/textures/gui/title/minecraft.png")) {
            inputStream = VanillaRecover.class.getResourceAsStream("/assets/minecraft/textures/gui/title/" + (VanillaRecover.getMcVersion().equals("1.7.10") ? "1" : "2") + "/minecraft.png");
            System.out.println("VanillaRecoverPack - getResourceAsStream(1) - " + inputStream + " - " + name);
        } else if (name.startsWith("/assets/minecraft/lang/") || name.startsWith("/assets/realms/lang/")) {
            inputStream = VanillaRecover.class.getResourceAsStream(StringUtils.substringBeforeLast(name, "/") + "/" + VanillaRecover.getMcMajorVersion() + "/" + StringUtils.substringAfterLast(name, "/"));
            System.out.println("VanillaRecoverPack - getResourceAsStream(2) - " + inputStream + " - " + name);
        }
        return inputStream == null ? clazz.getResourceAsStream(name) : inputStream;
    }

    public static InputStream readMetadata(InputStream _inputStream) {
        if (VanillaRecover.getMcMajorVersion().equals("1.9") || VanillaRecover.getMcMajorVersion().equals("1.10")) {
            return _inputStream;
        }
        InputStream inputStream = VanillaRecover.class.getResourceAsStream("/packmeta/" + VanillaRecover.getMcMajorVersion() + "/pack.mcmeta");
        System.out.println("VanillaRecoverPack - readMetadata - " + inputStream);
        return inputStream == null ? _inputStream : inputStream;
    }
}
