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
		maxDistance = loadPropInt("最大距离", maxDistance);
		renderInF1 = loadPropBool("隐藏界面时仍显示 (F1)", renderInF1);
		heightAbove = loadPropDouble("离生物的高度", heightAbove);
		drawBackground = loadPropBool("显示背景", drawBackground);
		backgroundPadding = loadPropInt("背景衬底", backgroundPadding);
		backgroundHeight = loadPropInt("背景高度", backgroundHeight);
		barHeight = loadPropInt("血量条高度", barHeight);
		plateSize = loadPropInt("面板大小", plateSize);
		plateSizeBoss = loadPropInt("面板大小（Boss）", plateSizeBoss);
		showAttributes = loadPropBool("显示属性", showAttributes);
		showArmor = loadPropBool("显示盔甲", showArmor);
		groupArmor = loadPropBool("合并盔甲（5个铁图标合并成1个钻石图标）", groupArmor);
		colorByType = loadPropBool("按类型显示血量条颜色（而不是血量百分比）", colorByType);
		hpTextHeight = loadPropInt("血量文字高度", hpTextHeight);
		showMaxHP = loadPropBool("显示最大血量", showMaxHP);
		showCurrentHP = loadPropBool("显示当前血量", showCurrentHP);
		showPercentage = loadPropBool("显示血量百分比", showPercentage);
		showOnPlayers = loadPropBool("显示玩家生物血量", showOnPlayers);
		showOnBosses = loadPropBool("显示Boss级生物血量", showOnBosses);
		showOnlyFocused = loadPropBool("瞄准实体时才显示血量条", showOnlyFocused);
		enableDebugInfo = loadPropBool("在F3中显示调试信息", enableDebugInfo);

		Property prop = config.get(Configuration.CATEGORY_GENERAL, "黑名单", new String[] { "潜影贝", "盔甲架" });
		prop.setComment("黑名单使用实体ID，而不是它的显示名。按下F3可以在Neat条中看到。");
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
