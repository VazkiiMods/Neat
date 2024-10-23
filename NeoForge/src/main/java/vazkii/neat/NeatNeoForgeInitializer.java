package vazkii.neat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(NeatConfig.MOD_ID)
public class NeatNeoForgeInitializer {

	public NeatNeoForgeInitializer(IEventBus bus, ModContainer modContainer) {
		//ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
		NeatNeoForgeConfig.init(modContainer);
	}

}
