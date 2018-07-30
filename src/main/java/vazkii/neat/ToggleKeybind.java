package vazkii.neat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ToggleKeybind {

	KeyBinding key;
	boolean down;
	
	public ToggleKeybind() {
		key = new KeyBinding("neat.keybind.toggle", 0, "key.categories.misc");
		ClientRegistry.registerKeyBinding(key);
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean wasDown = down;
		down = key.isKeyDown();
		if(mc.inGameHasFocus && down && !wasDown)
			NeatConfig.draw = !NeatConfig.draw;
	}
	
}
