package vazkii.neat;

import net.minecraft.world.entity.MobCategory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import static io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes.*;
import static io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes.STRING;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public final class NeatFiberConfig {
	private static final Logger LOGGER = LogManager.getLogger(NeatFiberConfig.class);

	private static void writeDefaultConfig(ConfigTree config, Path path, JanksonValueSerializer serializer) {
		try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
			FiberSerialization.serialize(config, s, serializer);
		} catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
			LOGGER.error("Error writing default config", e);
		}
	}

	private static void setupConfig(ConfigTree config, Path p, JanksonValueSerializer serializer) {
		writeDefaultConfig(config, p, serializer);

		try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
			FiberSerialization.deserialize(config, s, serializer);
		} catch (IOException | ValueDeserializationException e) {
			LOGGER.error("Error loading config from {}", p, e);
		}
	}

	public static void setup() {
		try {
			Files.createDirectory(Paths.get("config"));
		} catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
			LOGGER.warn("Failed to make config dir", e);
		}

		JanksonValueSerializer serializer = new JanksonValueSerializer(false);
		ConfigTree client = CLIENT.configure(ConfigTree.builder());
		setupConfig(client, Paths.get("config", NeatConfig.MOD_ID + "-client.json5"), serializer);
		NeatConfig.instance = CLIENT;
	}

	private static class Client implements NeatConfig.ConfigAccess {
		private final PropertyMirror<Integer> maxDistance = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Integer> maxDistanceWithoutLineOfSight = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Boolean> renderInF1 = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Double> heightAbove = PropertyMirror.create(DOUBLE);
		private final PropertyMirror<Boolean> drawBackground = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Integer> backgroundPadding = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Integer> backgroundHeight = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Integer> barHeight = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Integer> plateSize = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Integer> plateSizeBoss = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Boolean> showAttributes = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showArmor = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> groupArmor = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> colorByType = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<String> textColor = PropertyMirror.create(STRING);
		private final PropertyMirror<Integer> hpTextHeight = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Boolean> showMaxHP = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showCurrentHP = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showPercentage = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnPassive = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnHostile = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnPlayers = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnBosses = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnlyFocused = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showFullHealth = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> enableDebugInfo = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showEntityName = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> disableNameTag = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<List<String>> blacklist = PropertyMirror.create(ConfigTypes.makeList(STRING));

		public ConfigTree configure(ConfigTreeBuilder builder) {
			builder.beginValue("maxDistance", INTEGER, 24)
					.withComment("Maximum distance in blocks at which health bars should render")
					.finishValue(maxDistance::mirror)

					.beginValue("maxDistanceWithoutLineOfSight", INTEGER, 8)
					.withComment("Maximum distance in blocks at which health bars should render without line of sight")
					.finishValue(maxDistanceWithoutLineOfSight::mirror)

					.beginValue("renderInF1", BOOLEAN, false)
					.withComment("Whether health bars should render when the HUD is disabled with F1")
					.finishValue(renderInF1::mirror)

					.beginValue("heightAbove", DOUBLE, 0.6)
					.withComment("How far above the mob health bars should render")
					.finishValue(heightAbove::mirror)

					.beginValue("drawBackground", BOOLEAN, true)
					.withComment("Whether the gray background plate should be drawn")
					.finishValue(drawBackground::mirror)

					.beginValue("backgroundPadding", INTEGER, 2)
					.withComment("Amount of extra padding space around the background plate")
					.finishValue(backgroundPadding::mirror)

					.beginValue("backgroundHeight", INTEGER, 6)
					.withComment("How tall the background plate should be")
					.finishValue(backgroundHeight::mirror)

					.beginValue("barHeight", INTEGER, 4)
					.withComment("How tall the health bar should be")
					.finishValue(barHeight::mirror)

					.beginValue("plateSize", INTEGER, 25)
					.withComment("How wide the health bar should be. If the entity has a long name, the bar will increase in size to match it.")
					.finishValue(plateSize::mirror)

					.beginValue("plateSizeBoss", INTEGER, 50)
					.withComment("plateSize but for bosses")
					.finishValue(plateSizeBoss::mirror)

					.beginValue("showAttributes", BOOLEAN, true)
					.withComment("Show mob attributes such as arthropod or undead")
					.finishValue(showAttributes::mirror)

					.beginValue("showArmor", BOOLEAN, true)
					.withComment("Show armor points")
					.finishValue(showArmor::mirror)

					.beginValue("groupArmor", BOOLEAN, true)
					.withComment("Group armor points into diamond icons")
					.finishValue(groupArmor::mirror)

					.beginValue("colorByType", BOOLEAN, false)
					.withComment("Color the bar differently depending on whether the entity is hostile or is a boss")
					.finishValue(colorByType::mirror)

					.beginValue("textColor", STRING, "FFFFFF")
					.withComment("Text color in hex code format")
					.finishValue(textColor::mirror)

					.beginValue("hpTextHeight", INTEGER, 14)
					.withComment("Height of the text on the health bar")
					.finishValue(hpTextHeight::mirror)

					.beginValue("showMaxHP", BOOLEAN, true)
					.withComment("Whether the maximum health of the mob should be shown")
					.finishValue(showMaxHP::mirror)

					.beginValue("showCurrentHP", BOOLEAN, true)
					.withComment("Whether the current health of the mob should be shown")
					.finishValue(showCurrentHP::mirror)

					.beginValue("showPercentage", BOOLEAN, true)
					.withComment("Whether the percentage health of the mob should be shown")
					.finishValue(showPercentage::mirror)

					.beginValue("showOnPassive", BOOLEAN, true)
					.withComment("Whether bars on passive mobs should be shown")
					.finishValue(showOnPassive::mirror)

					.beginValue("showOnHostile", BOOLEAN, true)
					.withComment("Whether bars on hostile mobs should be shown (does not include bosses)")
					.finishValue(showOnHostile::mirror)

					.beginValue("showOnPlayers", BOOLEAN, true)
					.withComment("Whether bars on players should be shown")
					.finishValue(showOnPlayers::mirror)

					.beginValue("showOnBosses", BOOLEAN, true)
					.withComment("Whether bars on bosses should be shown")
					.finishValue(showOnBosses::mirror)

					.beginValue("showOnlyFocused", BOOLEAN, false)
					.withComment("Only show bars for mobs you are targeting")
					.finishValue(showOnlyFocused::mirror)

					.beginValue("showFullHealth", BOOLEAN, true)
					.withComment("Show bars for mobs that are at full health")
					.finishValue(showFullHealth::mirror)

					.beginValue("enableDebugInfo", BOOLEAN, true)
					.withComment("Show extra debug info on the bar when F3 is enabled")
					.finishValue(enableDebugInfo::mirror)

					.beginValue("showEntityName", BOOLEAN, true)
					.withComment("Show entity name")
					.finishValue(showEntityName::mirror)

					.beginValue("disableNameTag", BOOLEAN, false)
					.withComment("Disables the rendering of the vanilla name tag")
					.finishValue(disableNameTag::mirror)

					.beginValue("blacklist", ConfigTypes.makeList(STRING), NeatConfig.DEFAULT_DISABLED)
					.withComment("Entity ID's that should not have bars rendered")
					.finishValue(blacklist::mirror);

			return builder.build();
		}

		@Override
		public int maxDistance() {
			return maxDistance.getValue();
		}

		@Override
		public int maxDistanceWithoutLineOfSight() {
			return maxDistanceWithoutLineOfSight.getValue();
		}

		@Override
		public boolean renderInF1() {
			return renderInF1.getValue();
		}

		@Override
		public double heightAbove() {
			return heightAbove.getValue();
		}

		@Override
		public boolean drawBackground() {
			return drawBackground.getValue();
		}

		@Override
		public int backgroundPadding() {
			return backgroundPadding.getValue();
		}

		@Override
		public int backgroundHeight() {
			return backgroundHeight.getValue();
		}

		@Override
		public int barHeight() {
			return barHeight.getValue();
		}

		@Override
		public int plateSize() {
			return plateSize.getValue();
		}

		@Override
		public int plateSizeBoss() {
			return plateSizeBoss.getValue();
		}

		@Override
		public boolean showAttributes() {
			return showAttributes.getValue();
		}

		@Override
		public boolean showArmor() {
			return showArmor.getValue();
		}

		@Override
		public boolean groupArmor() {
			return groupArmor.getValue();
		}

		@Override
		public boolean colorByType() {
			return colorByType.getValue();
		}

		@Override
		public String textColor() {
			return textColor.getValue();
		}

		@Override
		public int hpTextHeight() {
			return hpTextHeight.getValue();
		}

		@Override
		public boolean showMaxHP() {
			return showMaxHP.getValue();
		}

		@Override
		public boolean showCurrentHP() {
			return showCurrentHP.getValue();
		}

		@Override
		public boolean showPercentage() {
			return showPercentage.getValue();
		}

		@Override
		public boolean showOnPassive() {
			return showOnPassive.getValue();
		}

		@Override
		public boolean showOnHostile() {
			return showOnHostile.getValue();
		}

		@Override
		public boolean showOnPlayers() {
			return showOnPlayers.getValue();
		}

		@Override
		public boolean showOnBosses() {
			return showOnBosses.getValue();
		}

		@Override
		public boolean showOnlyFocused() {
			return showOnlyFocused.getValue();
		}

		@Override
		public boolean showFullHealth() {
			return showFullHealth.getValue();
		}

		@Override
		public boolean enableDebugInfo() {
			return enableDebugInfo.getValue();
		}

		@Override
		public boolean showEntityName() {
			return showEntityName.getValue();
		}

		@Override
		public boolean disableNameTag() {
			return disableNameTag.getValue();
		}

		@Override
		public List<String> blacklist() {
			return blacklist.getValue();
		}
	}

	private static final Client CLIENT = new Client();
}
