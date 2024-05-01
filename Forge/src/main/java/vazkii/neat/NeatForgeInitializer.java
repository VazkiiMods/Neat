package vazkii.neat;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(NeatConfig.MOD_ID)
public class NeatForgeInitializer {

    public NeatForgeInitializer() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (incoming, isNetwork) -> true));
        NeatForgeConfig.init();
    }

    @Mod.EventBusSubscriber(modid = NeatConfig.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEventHandler {
        @SubscribeEvent
        public static void registerKey(RegisterKeyMappingsEvent event) {
            event.register(ToggleKeybind.KEY);
        }
    }
}
