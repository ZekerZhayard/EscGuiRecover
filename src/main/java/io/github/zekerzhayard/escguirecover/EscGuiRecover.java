package io.github.zekerzhayard.escguirecover;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.SortingIndex(1002)
public class EscGuiRecover extends DummyModContainer implements IClassTransformer, IFMLLoadingPlugin {
    public EscGuiRecover() {
        super(new ModMetadata());
        ModMetadata md = this.getMetadata();
        md.modId = "escguirecover";
        md.name = "EscGuiRecover";
        md.version = "2.0.0";
        md.url = "https://github.com/ZekerZhayard/EscGuiRecover";
        md.updateJSON = "https://raw.githubusercontent.com/ZekerZhayard/EscGuiRecover/1.8.9/update.json";
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {(EscGuiRecover.class.getName())};
    }

    @Override
    public String getModContainerClass() {
        return EscGuiRecover.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public byte[] transform(final String className, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.Minecraft")) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            new ClassReader(basicClass).accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    if (FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, name, desc).equals("func_175609_am") && desc.equals("()V")) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                this.mv.visitVarInsn(Opcodes.ALOAD, 0);
                                this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, EscGuiRecover.class.getName().replace(".", "/"), "createDisplay", "(Lnet/minecraft/client/Minecraft;)V", false);
                                this.mv.visitInsn(Opcodes.RETURN);
                            }
                        };
                    }
                    return mv;
                }
            }, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
        return basicClass;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    public static void createDisplay(Minecraft mc) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, LWJGLException, NoSuchMethodException {
        Display.setResizable(true);
        Display.setTitle("Minecraft 1.8.9");
        try {
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException lwjglexception) {
            ((Logger) FieldUtils.readDeclaredStaticField(Minecraft.class, "field_147123_G", true)).error("Couldn\'t set pixel format", lwjglexception);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var3) {
                ;
            }
            if ((boolean) FieldUtils.readDeclaredField(mc, "field_71431_Q", true)) {
                Method updateDisplayMode = Minecraft.class.getDeclaredMethod("func_110441_Q");
                updateDisplayMode.setAccessible(true);
                updateDisplayMode.invoke(mc);
            }
            Display.create();
        }
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event) throws IllegalAccessException {
        if (event.gui instanceof GuiIngameMenu) {
            GuiButton guibutton;
            event.buttonList.add(guibutton = new GuiButton(7, event.gui.width / 2 - 100, event.gui.height / 4 + 56, 200, 20, I18n.format("menu.shareToLan")));
            guibutton.enabled = Minecraft.getMinecraft().isSingleplayer() && !Minecraft.getMinecraft().getIntegratedServer().getPublic();
            event.buttonList.add(new GuiButton(12, event.gui.width / 2 + 2, event.gui.height / 4 + 80, 98, 20, I18n.format("fml.menu.modoptions")));
        } else if (event.gui instanceof GuiMainMenu) {
            event.buttonList.add(new GuiButtonLanguage(5, event.gui.width / 2 - 124, event.gui.height / 4 + 132));
            if (!Minecraft.getMinecraft().isDemo()) {
                event.buttonList.add(new GuiButton(6, event.gui.width / 2 - 100, event.gui.height / 4 + 96, 98, 20, I18n.format("fml.menu.mods")));
                GuiButton realmsButton = (GuiButton) FieldUtils.readDeclaredField(event.gui, "field_175372_K", true);
                event.buttonList.add(realmsButton = new GuiButton(14, event.gui.width / 2 + 2, event.gui.height / 4 + 96, 98, 20, I18n.format("menu.online").replace("Minecraft", "").trim()));
                FieldUtils.writeDeclaredField(event.gui, "field_175372_K", realmsButton, true);
            }
        } else if (event.gui instanceof GuiOptions) {
            for (GuiButton button : event.buttonList) {
                if (button.id == 103) {
                    button.xPosition = event.gui.width / 2 + 5;
                    button.yPosition = event.gui.height / 6 + 114;
                } else if (button.id == 105) {
                    button.xPosition = event.gui.width / 2 - 155;
                    button.yPosition = event.gui.height / 6 + 138;
                }
            }
            event.buttonList.add(new GuiButton(102, event.gui.width / 2 - 155, event.gui.height / 6 + 114, 150, 20, I18n.format("options.language")));
            event.buttonList.add(new GuiButton(104, event.gui.width / 2 + 5, event.gui.height / 6 + 138, 150, 20, I18n.format("options.snooper.view")));
            event.buttonList.add(new GuiButton(107, event.gui.width / 2 + 5, event.gui.height / 6 + 72 - 6, 150, 20, I18n.format("options.stream")));
            event.buttonList.add(new GuiButton(110, event.gui.width / 2 - 155, event.gui.height / 6 + 42, 150, 20, I18n.format("options.skinCustomisation")));
        } 
    }
}