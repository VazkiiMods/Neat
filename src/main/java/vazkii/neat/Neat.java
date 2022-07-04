package vazkii.neat;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(Neat.MOD_ID)
public class Neat {

	public static final String MOD_ID = "neat";

	public Neat() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		NeatConfig.init();
	}

	public void setup(FMLClientSetupEvent event) {
		// Constructing KEY itself accesses global vanilla state which is incompatible with parallel mod loading
		event.enqueueWork(() -> ClientRegistry.registerKeyBinding(ToggleKeybind.KEY));
		MinecraftForge.EVENT_BUS.addListener((InputEvent.KeyInputEvent e) -> ToggleKeybind.onKeyInput());
		MinecraftForge.EVENT_BUS.addListener((RenderLevelLastEvent e) -> HealthBarRenderer.onRenderLevelLast(e.getPoseStack(), e.getPartialTick(), e.getProjectionMatrix()));
	}
}
