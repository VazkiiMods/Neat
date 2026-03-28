package vazkii.neat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Supplier;

public class NeatFabricConfig {

	public static void init() {
		AutoConfig.register(Client.class, JanksonConfigSerializer::new);
		NeatConfig.instance = AutoConfig.getConfigHolder(Client.class).getConfig();
	}

	public static Supplier<Screen> getConfigScreen(Screen parent) {
		return AutoConfig.getConfigScreen(Client.class, parent);
	}

	@Config(name = "neat-client")
	private static class Client implements NeatConfig.ConfigAccess, ConfigData {
		@Comment("Maximum distance in blocks at which health bars should render")
		private int maxDistance;
		@Comment("Maximum distance in blocks at which health bars should render without line of sight")
		private int maxDistanceWithoutLineOfSight;
		@Comment("Whether health bars should render when the HUD is disabled with F1")
		private boolean renderInF1;
		@Comment("How far above the mob the health bars should render")
		private double heightAbove;
		@Comment("Whether the gray background plate should be drawn")
		private boolean drawBackground;
		@Comment("Amount of extra padding space around the background plate")
		private int backgroundPadding;
		@Comment("How tall the background plate should be")
		private int backgroundHeight;
		@Comment("How tall the health bar should be")
		private int barHeight;
		@Comment("How wide the health bar should be. If the entity has a long name, the bar will increase in size to match it.")
		private int plateSize;
		@Comment("plateSize but for bosses")
		private int plateSizeBoss;
		@Comment("Show mob attributes such as arthropod or undead")
		private boolean showAttributes;
		@Comment("Show armor points")
		private boolean showArmor;
		@Comment("Group armor points into diamond icons")
		private boolean groupArmor;
		@Comment("Color health bar by mob type instead of health percentage")
		private boolean colorByType;
		@Comment("Text color in hex code format")
		private String textColor;
		@Comment("Height of the text on the health bar")
		private int hpTextHeight;
		@Comment("Whether the maximum health of the mob should be shown")
		private boolean showMaxHP;
		@Comment("Whether the current health of the mob should be shown")
		private boolean showCurrentHP;
		@Comment("Whether the percentage health of the mob should be shown")
		private boolean showPercentage;
		@Comment("Whether bars on passive mobs should be shown")
		private boolean showOnPassive;
		@Comment("Whether bars on hostile mobs should be shown (does not include bosses)")
		private boolean showOnHostile;
		@Comment("Whether bars on players should be shown")
		private boolean showOnPlayers;
		@Comment("Whether bars on bosses should be shown")
		private boolean showOnBosses;
		@Comment("Only show bars for mobs you are targeting")
		private boolean showOnlyFocused;
		@Comment("Show bars for mobs that are at full health")
		private boolean showFullHealth;
		@Comment("Show extra debug info on the bar when F3 is enabled")
		private boolean enableDebugInfo;
		@Comment("Show entity name")
		private boolean showEntityName;
		@Comment("Changes when the vanilla name tag should be rendered")
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		private NeatConfig.NameTagRenderBehavior nameTagRenderBehavior;
		@Comment("Offsets the healtbar icons on the x axis")
		private double iconOffsetX;
		@Comment("Offsets the healtbar icons on the y axis")
		private double iconOffsetY;
		@Comment("Changes the decimal format of the HP. Only change this value if you are familiar with how the decimal format works!")
		private String decimalFormat;
		@Comment("Entity ID's that should not have bars rendered")
		private List<String> blacklist;

		public Client() {
			maxDistance = 24;
			maxDistanceWithoutLineOfSight = 8;
			renderInF1 = false;
			heightAbove = 0.4D;
			drawBackground = true;
			backgroundPadding = 2;
			backgroundHeight = 6;
			barHeight = 4;
			plateSize = 25;
			plateSizeBoss = 50;
			showAttributes = true;
			showArmor = true;
			groupArmor = true;
			colorByType = false;
			textColor = "FFFFFF";
			hpTextHeight = 14;
			showMaxHP = true;
			showCurrentHP = true;
			showPercentage = true;
			showOnPassive = true;
			showOnHostile = true;
			showOnPlayers = true;
			showOnBosses = true;
			showOnlyFocused = false;
			showFullHealth = true;
			enableDebugInfo = true;
			showEntityName = true;
			nameTagRenderBehavior = NeatConfig.NameTagRenderBehavior.WHEN_NO_HEALTHBAR;
			iconOffsetX = 0.0;
			iconOffsetY = 0.0;
			decimalFormat = "#.##";
			blacklist = NeatConfig.DEFAULT_DISABLED;
		}

		@Override
		public int maxDistance() {
			return maxDistance;
		}

		@Override
		public int maxDistanceWithoutLineOfSight() {
			return maxDistanceWithoutLineOfSight;
		}

		@Override
		public boolean renderInF1() {
			return renderInF1;
		}

		@Override
		public double heightAbove() {
			return heightAbove;
		}

		@Override
		public boolean drawBackground() {
			return drawBackground;
		}

		@Override
		public int backgroundPadding() {
			return backgroundPadding;
		}

		@Override
		public int backgroundHeight() {
			return backgroundHeight;
		}

		@Override
		public int barHeight() {
			return barHeight;
		}

		@Override
		public int plateSize() {
			return plateSize;
		}

		@Override
		public int plateSizeBoss() {
			return plateSizeBoss;
		}

		@Override
		public boolean showAttributes() {
			return showAttributes;
		}

		@Override
		public boolean showArmor() {
			return showArmor;
		}

		@Override
		public boolean groupArmor() {
			return groupArmor;
		}

		@Override
		public boolean colorByType() {
			return colorByType;
		}

		@Override
		public String textColor() {
			return textColor;
		}

		@Override
		public int hpTextHeight() {
			return hpTextHeight;
		}

		@Override
		public boolean showMaxHP() {
			return showMaxHP;
		}

		@Override
		public boolean showCurrentHP() {
			return showCurrentHP;
		}

		@Override
		public boolean showPercentage() {
			return showPercentage;
		}

		@Override
		public boolean showOnPassive() {
			return showOnPassive;
		}

		@Override
		public boolean showOnHostile() {
			return showOnHostile;
		}

		@Override
		public boolean showOnPlayers() {
			return showOnPlayers;
		}

		@Override
		public boolean showOnBosses() {
			return showOnBosses;
		}

		@Override
		public boolean showOnlyFocused() {
			return showOnlyFocused;
		}

		@Override
		public boolean showFullHealth() {
			return showFullHealth;
		}

		@Override
		public boolean enableDebugInfo() {
			return enableDebugInfo;
		}

		@Override
		public boolean showEntityName() {
			return showEntityName;
		}

		@Override
		public NeatConfig.NameTagRenderBehavior nameTagRenderBehavior() {
			return nameTagRenderBehavior;
		}

		@Override
		public double iconOffsetX() {
			return iconOffsetX;
		}

		@Override
		public double iconOffsetY() {
			return iconOffsetY;
		}

		@Override
		public String decimalFormat() {
			return decimalFormat;
		}

		@Override
		public List<String> blacklist() {
			return blacklist;
		}
	}
}
