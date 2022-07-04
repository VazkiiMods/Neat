package vazkii.neat;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NeatConfig {

	public static boolean draw = true;

	public static ConfigValue<Integer> maxDistance;
	public static ConfigValue<Boolean> renderInF1;
	public static ConfigValue<Double> heightAbove;
	public static ConfigValue<Boolean> drawBackground;
	public static ConfigValue<Integer> backgroundPadding;
	public static ConfigValue<Integer> backgroundHeight;
	public static ConfigValue<Integer> barHeight;
	public static ConfigValue<Integer> plateSize;
	public static ConfigValue<Integer> plateSizeBoss;
	public static ConfigValue<Boolean> showAttributes;
	public static ConfigValue<Boolean> showArmor;
	public static ConfigValue<Boolean> groupArmor;
	public static ConfigValue<Boolean> colorByType;
	public static ConfigValue<Integer> hpTextHeight;
	public static ConfigValue<Boolean> showMaxHP;
	public static ConfigValue<Boolean> showCurrentHP;
	public static ConfigValue<Boolean> showPercentage;
	public static ConfigValue<Boolean> showOnPlayers;
	public static ConfigValue<Boolean> showOnBosses;
	public static ConfigValue<Boolean> showOnlyFocused;
	public static ConfigValue<Boolean> showFullHealth;
	public static ConfigValue<Boolean> enableDebugInfo;
	public static ConfigValue<List<? extends String>> blacklist;

	public static void init() {
		Pair<Loader, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Loader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
	}

	static class Loader {

		public Loader(ForgeConfigSpec.Builder builder) {
			builder.push("general");

			maxDistance = builder.define("Max Distance", 24);
			renderInF1 = builder.define("Render with Interface Disabled (F1)", false);
			heightAbove = builder.define("Height Above Mob", 0.6);
			drawBackground = builder.define("Draw Background", true);
			backgroundPadding = builder.define("Background Padding", 2);
			backgroundHeight = builder.define("Background Height", 6);
			barHeight = builder.define("Health Bar Height", 4);
			plateSize = builder.define("Plate Size", 25);
			plateSizeBoss = builder.define("Plate Size (Boss)", 50);
			showAttributes = builder.define("Show Attributes", true);
			showArmor = builder.define("Show Armor", true);
			groupArmor = builder.define("Group Armor (condense 5 iron icons into 1 diamond icon)", true);
			colorByType = builder.define("Color Health Bar by Type (instead of health percentage)", false);
			hpTextHeight = builder.define("HP Text Height", 14);
			showMaxHP = builder.define("Show Max HP", true);
			showCurrentHP = builder.define("Show Current HP", true);
			showPercentage = builder.define("Show HP Percentage", true);
			showOnPlayers = builder.define("Display on Players", true);
			showOnBosses = builder.define("Display on Bosses", true);
			showOnlyFocused = builder.define("Only show the health bar for the entity looked at", false);
			showFullHealth = builder.define("Show entities with full health", true);
			enableDebugInfo = builder.define("Show Debug Info with F3", true);
			blacklist = builder.comment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.")
					.defineList("Blacklist",
							ImmutableList.of("minecraft:shulker", "minecraft:armor_stand", "minecraft:cod", "minecraft:salmon", "minecraft:pufferfish", "minecraft:tropical_fish"),
							a -> true);

			builder.pop();
		}

	}

}
