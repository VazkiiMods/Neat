package vazkii.neat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NeatForgeConfig {
	public static void init() {
		Pair<ForgeNeatConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ForgeNeatConfig::new);
		NeatConfig.instance = specPair.getLeft();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
	}

	private static class ForgeNeatConfig implements NeatConfig.ConfigAccess {
		private final ConfigValue<Integer> maxDistance;
		private final ConfigValue<Integer> maxDistanceWithoutLineOfSight;
		private final ConfigValue<Boolean> renderInF1;
		private final ConfigValue<Double> heightAbove;
		private final ConfigValue<Boolean> drawBackground;
		private final ConfigValue<Integer> backgroundPadding;
		private final ConfigValue<Integer> backgroundHeight;
		private final ConfigValue<Integer> barHeight;
		private final ConfigValue<Integer> plateSize;
		private final ConfigValue<Integer> plateSizeBoss;
		private final ConfigValue<Boolean> showAttributes;
		private final ConfigValue<Boolean> showArmor;
		private final ConfigValue<Boolean> groupArmor;
		private final ConfigValue<Boolean> colorByType;
		private final ConfigValue<String> textColor;
		private final ConfigValue<Integer> hpTextHeight;
		private final ConfigValue<Boolean> showMaxHP;
		private final ConfigValue<Boolean> showCurrentHP;
		private final ConfigValue<Boolean> showPercentage;
		private final ConfigValue<Boolean> showOnPassive;
		private final ConfigValue<Boolean> showOnHostile;
		private final ConfigValue<Boolean> showOnPlayers;
		private final ConfigValue<Boolean> showOnBosses;
		private final ConfigValue<Boolean> showOnlyFocused;
		private final ConfigValue<Boolean> showFullHealth;
		private final ConfigValue<Boolean> enableDebugInfo;
		private final ConfigValue<Boolean> showEntityName;
		private final ConfigValue<Boolean> disableNameTag;
		private final ConfigValue<Boolean> disableNameTagIfHealthbar;
		private final ConfigValue<Double> iconOffsetX;
		private final ConfigValue<Double> iconOffsetY;
		private final ConfigValue<String> decimalFormat;
		private final ConfigValue<List<? extends String>> blacklist;

		public ForgeNeatConfig(ForgeConfigSpec.Builder builder) {
			builder.push("general");

			maxDistance = builder.comment("Maximum distance in blocks at which health bars should render").define("max_distance", 24);
			maxDistanceWithoutLineOfSight = builder.comment("Maximum distance in blocks at which health bars should render without line of sight").define("max_distance_without_line_of_sight", 8);
			renderInF1 = builder.comment("Whether health bars should render when the HUD is disabled with F1").define("render_without_gui", false);
			heightAbove = builder.comment("How far above the mob the health bars should render").define("height_above_mob", 0.6);
			drawBackground = builder.comment("Whether the gray background plate should be drawn").define("draw_background", true);
			backgroundPadding = builder.comment("Amount of extra padding space around the background plate").define("background_padding", 2);
			backgroundHeight = builder.comment("How tall the background plate should be").define("background_height", 6);
			barHeight = builder.comment("How tall the health bar should be").define("health_bar_height", 4);
			plateSize = builder.comment("How wide the health bar should be. If the entity has a long name, the bar will increase in size to match it.").define("plate_size", 25);
			plateSizeBoss = builder.comment("plateSize but for bosses").define("plate_size_boss", 50);
			showAttributes = builder.comment("Show mob attributes such as arthropod or undead").define("show_attributes", true);
			showArmor = builder.comment("Show armor points").define("show_armor", true);
			groupArmor = builder.comment("Group 5 iron icons into 1 diamond icon").define("group_armor", true);
			colorByType = builder.comment("Color health bar by mob type instead of health percentage").define("color_health_bar_by_type", false);
			textColor = builder.comment("Text color in hex code format").define("text_color", "FFFFFF");
			hpTextHeight = builder.comment("Height of the text on the health bar").define("hp_text_height", 14);
			showMaxHP = builder.comment("Whether the maximum health of the mob should be shown").define("show_max_hp", true);
			showCurrentHP = builder.comment("Whether the current health of the mob should be shown").define("show_current_hp", true);
			showPercentage = builder.comment("Whether the percentage health of the mob should be shown").define("show_hp_percentage", true);
			showOnPassive = builder.comment("Whether bars on passive mobs should be shown").define("show_on_passive", true);
			showOnHostile = builder.comment("Whether bars on hostile mobs should be shown (does not include bosses)").define("show_on_hostile", true);
			showOnPlayers = builder.comment("Whether bars on players should be shown").define("display_on_players", true);
			showOnBosses = builder.comment("Whether bars on bosses should be shown").define("display_on_bosses", true);
			showOnlyFocused = builder.comment("Only show bars for mobs you are targeting").define("only_health_bar_for_target", false);
			showFullHealth = builder.comment("Show bars for mobs that are at full health").define("show_entity_full_health", true);
			enableDebugInfo = builder.comment("Show extra debug info on the bar when F3 is enabled").define("show_debug_with_f3", true);
			showEntityName = builder.comment("Show entity name").define("show_entity_name", true);
			disableNameTag = builder.comment("Disables the rendering of the vanilla name tag").define("disable_name_tag", false);
			disableNameTagIfHealthbar = builder.comment("If this is enabled and the \"disableNameTag\" option is true, the vanilla nametag is only hidden if the mob has a Neat healthbar rendered").define("disable_name_tag_if_healthbar", true);
			iconOffsetX = builder.comment("Offsets the healtbar icons on the x axis").define("icon_offset_x", 0.0);
			iconOffsetY = builder.comment("Offsets the healtbar icons on the y axis").define("icon_offset_y", 0.0);
			decimalFormat = builder.comment("This value changes the decimal format of the HP. Only change this value if you are familiar with how the decimal format works!").define("decimal_format", "#.##");
			blacklist = builder.comment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.")
					.defineList("blacklist", NeatConfig.DEFAULT_DISABLED, a -> true);

			builder.pop();
		}

		@Override
		public int maxDistance() {
			return maxDistance.get();
		}

		@Override
		public int maxDistanceWithoutLineOfSight() {
			return maxDistanceWithoutLineOfSight.get();
		}

		@Override
		public boolean renderInF1() {
			return renderInF1.get();
		}

		@Override
		public double heightAbove() {
			return heightAbove.get();
		}

		@Override
		public boolean drawBackground() {
			return drawBackground.get();
		}

		@Override
		public int backgroundPadding() {
			return backgroundPadding.get();
		}

		@Override
		public int backgroundHeight() {
			return backgroundHeight.get();
		}

		@Override
		public int barHeight() {
			return barHeight.get();
		}

		@Override
		public int plateSize() {
			return plateSize.get();
		}

		@Override
		public int plateSizeBoss() {
			return plateSizeBoss.get();
		}

		@Override
		public boolean showAttributes() {
			return showAttributes.get();
		}

		@Override
		public boolean showArmor() {
			return showArmor.get();
		}

		@Override
		public boolean groupArmor() {
			return groupArmor.get();
		}

		@Override
		public boolean colorByType() {
			return colorByType.get();
		}

		@Override
		public String textColor() {
			return textColor.get();
		}

		@Override
		public int hpTextHeight() {
			return hpTextHeight.get();
		}

		@Override
		public boolean showMaxHP() {
			return showMaxHP.get();
		}

		@Override
		public boolean showCurrentHP() {
			return showCurrentHP.get();
		}

		@Override
		public boolean showPercentage() {
			return showPercentage.get();
		}

		@Override
		public boolean showOnPassive() {
			return showOnPassive.get();
		}

		@Override
		public boolean showOnHostile() {
			return showOnHostile.get();
		}

		@Override
		public boolean showOnPlayers() {
			return showOnPlayers.get();
		}

		@Override
		public boolean showOnBosses() {
			return showOnBosses.get();
		}

		@Override
		public boolean showOnlyFocused() {
			return showOnlyFocused.get();
		}

		@Override
		public boolean showFullHealth() {
			return showFullHealth.get();
		}

		@Override
		public boolean enableDebugInfo() {
			return enableDebugInfo.get();
		}

		@Override
		public boolean showEntityName() {
			return showEntityName.get();
		}

		@Override
		public boolean disableNameTag() {
			return disableNameTag.get();
		}

		@Override
		public boolean disableNameTagIfHealthbar() {
			return disableNameTagIfHealthbar.get();
		}

		@Override
		public double iconOffsetX() {
			return iconOffsetX.get();
		}

		@Override
		public double iconOffsetY() {
			return iconOffsetY.get();
		}

		@Override
		public String decimalFormat() {
			return decimalFormat.get();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<String> blacklist() {
			// Safe cast from List<? extends String> to List<String>, as String is final
			return (List<String>) blacklist.get();
		}
	}
}
