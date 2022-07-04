package vazkii.neat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class NeatFabricInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NeatFiberConfig.setup();
		KeyBindingHelper.registerKeyBinding(ToggleKeybind.KEY);
	}
}
