package io.github.zekerzhayard.vanillarecover.hooks;

import java.io.InputStream;

import io.github.zekerzhayard.vanillarecover.VanillaRecover;
import org.apache.commons.lang3.StringUtils;

public class FileResourcePackHook {
    public static InputStream getInputStreamByName(String name) {
        if (name.startsWith("assets/forge/lang/")) {
            return VanillaRecover.class.getResourceAsStream("/assets/forge/lang/" + VanillaRecover.getMcVersion() + "/" + StringUtils.substringAfterLast(name, "/"));
        }
        return null;
    }
}
