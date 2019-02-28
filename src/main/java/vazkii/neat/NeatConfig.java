package vazkii.neat;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class NeatConfig {

	public static boolean draw = true;

	private static ConfigValue<Integer> v_maxDistance;
	private static ConfigValue<Boolean> v_renderInF1;
	private static ConfigValue<Double> v_heightAbove;
	private static ConfigValue<Boolean> v_drawBackground;
	private static ConfigValue<Integer> v_backgroundPadding;
	private static ConfigValue<Integer> v_backgroundHeight;
	private static ConfigValue<Integer> v_barHeight;
	private static ConfigValue<Integer> v_plateSize;
	private static ConfigValue<Integer> v_plateSizeBoss;
	private static ConfigValue<Boolean> v_showAttributes;
	private static ConfigValue<Boolean> v_showArmor;
	private static ConfigValue<Boolean> v_groupArmor;
	private static ConfigValue<Boolean> v_colorByType;
	private static ConfigValue<Integer> v_hpTextHeight;
	private static ConfigValue<Boolean> v_showMaxHP;
	private static ConfigValue<Boolean> v_showCurrentHP;
	private static ConfigValue<Boolean> v_showPercentage;
	private static ConfigValue<Boolean> v_showOnPlayers;
	private static ConfigValue<Boolean> v_showOnBosses;
	private static ConfigValue<Boolean> v_showOnlyFocused;
	private static ConfigValue<Boolean> v_enableDebugInfo;
	private static ConfigValue<List<? extends String>> v_blacklist;

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

	public static void init() {
		Pair<Loader, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Loader::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		maxDistance = v_maxDistance.get();
		renderInF1 = v_renderInF1.get();
		heightAbove = v_heightAbove.get();
		drawBackground = v_drawBackground.get();
		backgroundPadding = v_backgroundPadding.get();
		backgroundHeight = v_backgroundHeight.get();
		barHeight = v_barHeight.get();
		plateSize = v_plateSize.get();
		plateSizeBoss = v_plateSizeBoss.get();
		showAttributes = v_showAttributes.get();
		showArmor = v_showArmor.get();
		groupArmor = v_groupArmor.get();
		colorByType = v_colorByType.get();
		hpTextHeight = v_hpTextHeight.get();
		showMaxHP = v_showMaxHP.get();
		showCurrentHP = v_showCurrentHP.get();
		showPercentage = v_showPercentage.get();
		showOnPlayers = v_showOnPlayers.get();
		showOnBosses = v_showOnBosses.get();
		showOnlyFocused = v_showOnlyFocused.get();
		enableDebugInfo = v_enableDebugInfo.get();
		blacklist = (List<String>) v_blacklist.get();
	}
	
	static class Loader {
		
		public Loader(ForgeConfigSpec.Builder builder) {
			builder.push("general");

			v_maxDistance = builder.define("Max Distance", maxDistance);
			v_renderInF1 = builder.define("Render with Interface Disabled (F1)", renderInF1);
			v_heightAbove = builder.define("Height Above Mob", heightAbove);
			v_drawBackground = builder.define("Draw Background", drawBackground);
			v_backgroundPadding = builder.define("Background Padding", backgroundPadding);
			v_backgroundHeight = builder.define("Background Height", backgroundHeight);
			v_barHeight = builder.define("Health Bar Height", barHeight);
			v_plateSize = builder.define("Plate Size", plateSize);
			v_plateSizeBoss = builder.define("Plate Size (Boss)", plateSizeBoss);
			v_showAttributes = builder.define("Show Attributes", showAttributes);
			v_showArmor = builder.define("Show Armor", showArmor);
			v_groupArmor = builder.define("Group Armor (condense 5 iron icons into 1 diamond icon)", groupArmor);
			v_colorByType = builder.define("Color Health Bar by Type (instead of health percentage)", colorByType);
			v_hpTextHeight = builder.define("HP Text Height", hpTextHeight);
			v_showMaxHP = builder.define("Show Max HP", showMaxHP);
			v_showCurrentHP = builder.define("Show Current HP", showCurrentHP);
			v_showPercentage = builder.define("Show HP Percentage", showPercentage);
			v_showOnPlayers = builder.define("Display on Players", showOnPlayers);
			v_showOnBosses = builder.define("Display on Bosses", showOnBosses);
			v_showOnlyFocused = builder.define("Only show the health bar for the entity looked at", showOnlyFocused);
			v_enableDebugInfo = builder.define("Show Debug Info with F3", enableDebugInfo);
			v_blacklist = builder.comment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.")
					.defineList("Blacklist", 
							ImmutableList.of("minecraft:shulker", "minecraft:armor_stand", "minecraft:cod", "minecraft:salmon", "minecraft:pufferfish", "minecraft:tropical_fish"),
							a -> true);

			builder.pop();
		}
		
	}

}
