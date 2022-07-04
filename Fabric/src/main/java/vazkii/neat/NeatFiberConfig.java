package vazkii.neat;

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
		private final PropertyMirror<Integer> hpTextHeight = PropertyMirror.create(INTEGER);
		private final PropertyMirror<Boolean> showMaxHP = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showCurrentHP = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showPercentage = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnPlayers = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnBosses = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showOnlyFocused = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> showFullHealth = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<Boolean> enableDebugInfo = PropertyMirror.create(BOOLEAN);
		private final PropertyMirror<List<String>> blacklist = PropertyMirror.create(ConfigTypes.makeList(STRING));

		public ConfigTree configure(ConfigTreeBuilder builder) {
			builder.beginValue("maxDistance", INTEGER, 24).finishValue(maxDistance::mirror)
					.beginValue("renderInF1", BOOLEAN, false).finishValue(renderInF1::mirror)
					.beginValue("heightAbove", DOUBLE, 0.6).finishValue(heightAbove::mirror)
					.beginValue("drawBackground", BOOLEAN, true).finishValue(drawBackground::mirror)
					.beginValue("backgroundPadding", INTEGER, 2).finishValue(backgroundPadding::mirror)
					.beginValue("backgroundHeight", INTEGER, 6).finishValue(backgroundHeight::mirror)
					.beginValue("barHeight", INTEGER, 4).finishValue(barHeight::mirror)
					.beginValue("plateSize", INTEGER, 25).finishValue(plateSize::mirror)
					.beginValue("plateSizeBoss", INTEGER, 50).finishValue(plateSizeBoss::mirror)
					.beginValue("showAttributes", BOOLEAN, true).finishValue(showAttributes::mirror)
					.beginValue("showArmor", BOOLEAN, true).finishValue(showArmor::mirror)
					.beginValue("groupArmor", BOOLEAN, true).finishValue(groupArmor::mirror)
					.beginValue("colorByType", BOOLEAN, false).finishValue(colorByType::mirror)
					.beginValue("hpTextHeight", INTEGER, 14).finishValue(hpTextHeight::mirror)
					.beginValue("showMaxHP", BOOLEAN, true).finishValue(showMaxHP::mirror)
					.beginValue("showCurrentHP", BOOLEAN, true).finishValue(showCurrentHP::mirror)
					.beginValue("showPercentage", BOOLEAN, true).finishValue(showPercentage::mirror)
					.beginValue("showOnPlayers", BOOLEAN, true).finishValue(showOnPlayers::mirror)
					.beginValue("showOnBosses", BOOLEAN, true).finishValue(showOnBosses::mirror)
					.beginValue("showOnlyFocused", BOOLEAN, false).finishValue(showOnlyFocused::mirror)
					.beginValue("showFullHealth", BOOLEAN, true).finishValue(showFullHealth::mirror)
					.beginValue("enableDebugInfo", BOOLEAN, true).finishValue(enableDebugInfo::mirror)
					.beginValue("blacklist", ConfigTypes.makeList(STRING), NeatConfig.DEFAULT_DISABLED).finishValue(blacklist::mirror);

			return builder.build();
		}

		@Override
		public int maxDistance() {
			return maxDistance.getValue();
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
		public List<String> blacklist() {
			return blacklist.getValue();
		}
	}

	private static final Client CLIENT = new Client();
}
