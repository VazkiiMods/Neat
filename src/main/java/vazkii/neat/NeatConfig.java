package vazkii.neat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NeatConfig {
	
	public static boolean draw = true;
	
	public static int maxDistance = 24;
	public static boolean renderInF1 = false;
	public static double heightAbove = 0.6;
	public static boolean drawBackground = true;
	public static int backgroundPadding = 2;
	public static int backgroundHeight = 6;
	public static int barHeight = 4;
	public static int plateSize = 25;
	public static int plateSizeBoss = 50;
	public static boolean showAttributes = true;
	public static boolean showArmor = true;
	public static boolean groupArmor = true;
	public static boolean colorByType = false;
	public static int hpTextHeight = 14;
	public static boolean showMaxHP = true;
	public static boolean showCurrentHP = true;
	public static boolean showPercentage = true;
	public static boolean showOnPlayers = true;
	public static boolean showOnBosses = true;
	public static boolean showOnlyFocused = false;
	public static boolean enableDebugInfo = true;

	public static List<String> blacklist;
	
	public static Configuration config;
	
	public static void init(File configFile) {
		config = new Configuration(configFile);

		config.load();
		load();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}
	
	public static void load() {
		maxDistance = loadPropInt("Max Distance", maxDistance);
		renderInF1 = loadPropBool("Render with Interface Disabled (F1)", renderInF1);
		heightAbove = loadPropDouble("Height Above Mob", heightAbove);
		drawBackground = loadPropBool("Draw Background", drawBackground);
		backgroundPadding = loadPropInt("Background Padding", backgroundPadding);
		backgroundHeight = loadPropInt("Background Height", backgroundHeight);
		barHeight = loadPropInt("Health Bar Height", barHeight);
		plateSize = loadPropInt("Plate Size", plateSize);
		plateSizeBoss = loadPropInt("Plate Size (Boss)", plateSizeBoss);
		showAttributes = loadPropBool("Show Attributes", showAttributes);
		showArmor = loadPropBool("Show Armor", showArmor);
		groupArmor = loadPropBool("Group Armor (condense 5 iron icons into 1 diamond icon)", groupArmor);
		colorByType = loadPropBool("Color Health Bar by Type (instead of health percentage)", colorByType);
		hpTextHeight = loadPropInt("HP Text Height", hpTextHeight);
		showMaxHP = loadPropBool("Show Max HP", showMaxHP);
		showCurrentHP = loadPropBool("Show Current HP", showCurrentHP);
		showPercentage = loadPropBool("Show HP Percentage", showPercentage);
		showOnPlayers = loadPropBool("Display on Players", showOnPlayers);
		showOnBosses = loadPropBool("Display on Bosses", showOnBosses);
		showOnlyFocused = loadPropBool("Only show the health bar for the entity looked at", showOnlyFocused);
		enableDebugInfo = loadPropBool("Show Debug Info with F3", enableDebugInfo);

		Property prop = config.get(Configuration.CATEGORY_GENERAL, "Blacklist", new String[] { "Shulker", "ArmorStand" });
		prop.setComment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.");
		blacklist = Arrays.asList(prop.getStringList());
		
		if(config.hasChanged())
			config.save();
	}
	
	public static int loadPropInt(String propName, int default_) {
		Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
		return prop.getInt(default_);
	}

	public static double loadPropDouble(String propName, double default_) {
		Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
		return prop.getDouble(default_);
	}

	public static boolean loadPropBool(String propName, boolean default_) {
		Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
		return prop.getBoolean(default_);
	}
	
	public static class ChangeListener {

		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(Neat.MOD_ID))
				load();
		}

	}
}
