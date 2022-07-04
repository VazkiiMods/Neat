package vazkii.neat;

import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

public class ToggleKeybind {

	public static final KeyMapping KEY = new KeyMapping("neat.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");;

	public static void handle() {
		while (KEY.consumeClick()) {
			NeatConfig.draw = !NeatConfig.draw;
		}
	}

}
