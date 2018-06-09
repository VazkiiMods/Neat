package vazkii.neat;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// NO-OP
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGui(parentScreen);
	}
	
	public static class ConfigGui extends GuiConfig {

		public ConfigGui(GuiScreen parentScreen) {
			super(parentScreen, new ConfigElement(NeatConfig.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Neat.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(NeatConfig.config.toString()));
		}

	}

}
