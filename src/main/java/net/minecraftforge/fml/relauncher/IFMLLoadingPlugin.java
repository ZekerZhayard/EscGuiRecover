package net.minecraftforge.fml.relauncher;

import java.util.Map;

public interface IFMLLoadingPlugin {
    void injectData(Map<String, Object> data);

    String getSetupClass();

    String getModContainerClass();

    String getAccessTransformerClass();

    String[] getASMTransformerClass();
}
