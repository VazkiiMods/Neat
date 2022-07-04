package vazkii.neat;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public class ToggleKeybind {

	public static final KeyMapping KEY = new KeyMapping("neat.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");;
	private static boolean down;

	public static void onKeyInput() {
		Minecraft mc = Minecraft.getInstance();
		boolean wasDown = down;
		down = KEY.isDown();
		if (mc.isWindowActive() && down && !wasDown) {
			NeatConfig.draw = !NeatConfig.draw;
		}
	}

}
