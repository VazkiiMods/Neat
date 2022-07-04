package vazkii.neat;

import net.fabricmc.api.ClientModInitializer;

public class NeatFabricInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NeatFiberConfig.setup();
	}
}
