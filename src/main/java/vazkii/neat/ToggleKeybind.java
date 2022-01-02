package vazkii.neat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.ClientRegistry;

import org.lwjgl.glfw.GLFW;

public class ToggleKeybind {

	KeyMapping key;
	boolean down;

	public ToggleKeybind() {
		key = new KeyMapping("neat.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");
		ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		boolean wasDown = down;
		down = key.isDown();
		if (mc.isWindowActive() && down && !wasDown)
			NeatConfig.draw = !NeatConfig.draw;
	}

}
