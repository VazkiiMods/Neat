package vazkii.neat;

import java.util.List;

public class NeatConfig {

	public static final String MOD_ID = "neat";
	public static boolean draw = true;

	public interface ConfigAccess {
		int maxDistance();
		int maxDistanceWithoutLineOfSight();
		boolean renderInF1();
		double heightAbove();
		boolean drawBackground();
		int backgroundPadding();
		int backgroundHeight();
		int barHeight();
		int plateSize();
		int plateSizeBoss();
		boolean showAttributes();
		boolean showArmor();
		boolean groupArmor();
		boolean colorByType();
		String textColor();
		int hpTextHeight();
		boolean showMaxHP();
		boolean showCurrentHP();
		boolean showPercentage();
		boolean showOnPassive();
		boolean showOnHostile();
		boolean showOnPlayers();
		boolean showOnBosses();
		boolean showOnlyFocused();
		boolean showFullHealth();
		boolean enableDebugInfo();
		boolean showEntityName();
		NameTagRenderBehavior nameTagRenderBehavior();
		double iconOffsetX();
		double iconOffsetY();
		String decimalFormat();
		List<String> blacklist();
	}

	public static final List<String> DEFAULT_DISABLED = List.of("minecraft:shulker", "minecraft:armor_stand", "minecraft:cod", "minecraft:salmon", "minecraft:pufferfish", "minecraft:tropical_fish", "minecraft:tadpole");

	public enum NameTagRenderBehavior {
		ALWAYS,
		NEVER,
		WHEN_NO_HEALTHBAR
	}

	public static ConfigAccess instance;
}
