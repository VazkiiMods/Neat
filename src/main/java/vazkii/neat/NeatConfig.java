package vazkii.neat;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class NeatConfig {
	
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

	private static Configuration config;
	
	public static void init(File f) {
		config = new Configuration(f);
		config.load();
		
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
		colorByType = loadPropBool("Color Health Bar by Type (instead of health %)", colorByType);
		hpTextHeight = loadPropInt("HP Text Height", hpTextHeight);
		showMaxHP = loadPropBool("Show Max HP", showMaxHP);
		showCurrentHP = loadPropBool("Show Current HP", showCurrentHP);
		showPercentage = loadPropBool("Show HP Percentage", showPercentage);
		showOnPlayers = loadPropBool("Display on Players", showOnPlayers);
		showOnBosses = loadPropBool("Display on Bosses", showOnBosses);

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
}
