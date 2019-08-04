package vazkii.neat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ToggleKeybind {

	KeyBinding key;
	boolean down;
	
	public ToggleKeybind() {
		key = new KeyBinding("neat.keybind.toggle", 0, "key.categories.misc");
		ClientRegistry.registerKeyBinding(key);
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		boolean wasDown = down;
		down = key.isKeyDown();
		if(mc.isGameFocused() && down && !wasDown)
			NeatConfig.draw = !NeatConfig.draw;
	}
	
}
