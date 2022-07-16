package vazkii.neat;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(NeatConfig.MOD_ID)
public class NeatForgeInitializer {

	public NeatForgeInitializer() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKey);
		NeatForgeConfig.init();
	}

	private void registerKey(RegisterKeyMappingsEvent event) {
		event.register(ToggleKeybind.KEY);
	}
}
