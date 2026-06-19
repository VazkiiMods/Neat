package vazkii.neat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class NeatFabricInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NeatFabricConfig.init();
		KeyMappingHelper.registerKeyMapping(ToggleKeybind.KEY);
	}
}
