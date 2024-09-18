package vazkii.neat;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NeatNeoForgeConfig {
	public static void init(ModContainer container) {
		Pair<ForgeNeatConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ForgeNeatConfig::new);
		NeatConfig.instance = specPair.getLeft();
		container.registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
	}

	private static class ForgeNeatConfig implements NeatConfig.ConfigAccess {
		private final ModConfigSpec.ConfigValue<Integer> maxDistance;
		private final ModConfigSpec.ConfigValue<Integer> maxDistanceWithoutLineOfSight;
		private final ModConfigSpec.ConfigValue<Boolean> renderInF1;
		private final ModConfigSpec.ConfigValue<Double> heightAbove;
		private final ModConfigSpec.ConfigValue<Boolean> drawBackground;
		private final ModConfigSpec.ConfigValue<Integer> backgroundPadding;
		private final ModConfigSpec.ConfigValue<Integer> backgroundHeight;
		private final ModConfigSpec.ConfigValue<Integer> barHeight;
		private final ModConfigSpec.ConfigValue<Integer> plateSize;
		private final ModConfigSpec.ConfigValue<Integer> plateSizeBoss;
		private final ModConfigSpec.ConfigValue<Boolean> showAttributes;
		private final ModConfigSpec.ConfigValue<Boolean> showArmor;
		private final ModConfigSpec.ConfigValue<Boolean> groupArmor;
		private final ModConfigSpec.ConfigValue<Boolean> colorByType;
		private final ModConfigSpec.ConfigValue<String> textColor;
		private final ModConfigSpec.ConfigValue<Integer> hpTextHeight;
		private final ModConfigSpec.ConfigValue<Boolean> showMaxHP;
		private final ModConfigSpec.ConfigValue<Boolean> showCurrentHP;
		private final ModConfigSpec.ConfigValue<Boolean> showPercentage;
		private final ModConfigSpec.ConfigValue<Boolean> showOnPassive;
		private final ModConfigSpec.ConfigValue<Boolean> showOnHostile;
		private final ModConfigSpec.ConfigValue<Boolean> showOnPlayers;
		private final ModConfigSpec.ConfigValue<Boolean> showOnBosses;
		private final ModConfigSpec.ConfigValue<Boolean> showOnlyFocused;
		private final ModConfigSpec.ConfigValue<Boolean> showFullHealth;
		private final ModConfigSpec.ConfigValue<Boolean> showEntityName;
		private final ModConfigSpec.ConfigValue<Boolean> disableNameTag;
		private final ModConfigSpec.ConfigValue<Boolean> enableDebugInfo;
		private final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

		public ForgeNeatConfig(ModConfigSpec.Builder builder) {
			builder.push("general");

			maxDistance = builder.define("max_distance", 24);
			maxDistanceWithoutLineOfSight = builder.define("max_distance_without_line_of_sight", 8);
			renderInF1 = builder.comment("Render if F1 is pressed").define("render_without_gui", false);
			heightAbove = builder.define("height_above_mob", 0.6);
			drawBackground = builder.define("draw_background", true);
			backgroundPadding = builder.define("background_padding", 2);
			backgroundHeight = builder.define("background_height", 6);
			barHeight = builder.define("health_bar_height", 4);
			plateSize = builder.define("plate_size", 25);
			plateSizeBoss = builder.define("plate_size_boss", 50);
			showAttributes = builder.define("show_attributes", true);
			showArmor = builder.define("show_armor", true);
			groupArmor = builder.comment("Condense 5 iron icons into 1 diamond icon").define("group_armor", true);
			colorByType = builder.comment("Color health bar by type instead of health percentage").define("color_health_bar_by_type", false);
			textColor = builder.comment("Text color in hex code format").define("text_color", "FFFFFF");
			hpTextHeight = builder.define("hp_text_height", 14);
			showMaxHP = builder.define("show_max_hp", true);
			showCurrentHP = builder.define("show_current_hp", true);
			showPercentage = builder.define("show_hp_percentage", true);
			showOnPassive = builder.comment("Whether bars on passive mobs should be shown").define("show_on_passive", true);
			showOnHostile = builder.comment("Whether bars on hostile mobs should be shown (does not include bosses)").define("show_on_hostile", true);
			showOnPlayers = builder.define("display_on_players", true);
			showOnBosses = builder.define("display_on_bosses", true);
			showOnlyFocused = builder.define("only_health_bar_for_target", false);
			showFullHealth = builder.define("show_entity_full_health", true);
			enableDebugInfo = builder.define("show_debug_with_f3", true);
			showEntityName = builder.define("show_entity_name", true);
			disableNameTag = builder.comment("Disables the rendering of the vanilla name tag").define("disable_name_tag", false);
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

		@SuppressWarnings("unchecked")
		@Override
		public List<String> blacklist() {
			// Safe cast from List<? extends String> to List<String>, as String is final
			return (List<String>) blacklist.get();
		}
	}
}
