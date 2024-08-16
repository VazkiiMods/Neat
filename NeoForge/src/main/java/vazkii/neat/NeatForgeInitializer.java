package vazkii.neat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(NeatConfig.MOD_ID)
public class NeatForgeInitializer {

	public NeatForgeInitializer(IEventBus bus, ModContainer modContainer) {
		//ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
		NeatForgeConfig.init(modContainer);
	}

}
