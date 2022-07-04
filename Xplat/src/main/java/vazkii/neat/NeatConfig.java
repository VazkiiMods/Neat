package vazkii.neat;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class NeatConfig {

	public static final String MOD_ID = "neat";
	public static boolean draw = true;

	public interface ConfigAccess {
		int maxDistance();
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
		int hpTextHeight();
		boolean showMaxHP();
		boolean showCurrentHP();
		boolean showPercentage();
		boolean showOnPlayers();
		boolean showOnBosses();
		boolean showOnlyFocused();
		boolean showFullHealth();
		boolean enableDebugInfo();
		List<String> blacklist();
	}

	public static final List<String> DEFAULT_DISABLED = ImmutableList.of("minecraft:shulker", "minecraft:armor_stand", "minecraft:cod", "minecraft:salmon", "minecraft:pufferfish", "minecraft:tropical_fish");

	public static ConfigAccess instance;
}
