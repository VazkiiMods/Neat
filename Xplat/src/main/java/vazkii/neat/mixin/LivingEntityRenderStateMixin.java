package vazkii.neat.mixin;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.FormattedCharSequence;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import vazkii.neat.NeatRenderState;
import vazkii.neat.NeatTypeIcon;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements NeatRenderState {
	@Unique
	public boolean neat$isBoss;
	@Unique
	public boolean neat$isFriendly;
	@Unique
	public boolean neat$isIdBlacklisted;
	@Unique
	public boolean neat$shouldShowPlate;
	@Unique
	public FormattedCharSequence neat$nameToRender;
	@Unique
	public float neat$health;
	@Unique
	public float neat$maxHealth;
	@Unique
	public NeatTypeIcon neat$typeIcon;
	@Unique
	public int neat$armorValue;

	@Override
	public boolean neat$isBoss() {
		return neat$isBoss;
	}

	@Override
	public void neat$setBoss(boolean value) {
		this.neat$isBoss = value;
	}

	@Override
	public boolean neat$isFriendly() {
		return neat$isFriendly;
	}

	@Override
	public void neat$setFriendly(boolean value) {
		this.neat$isFriendly = value;
	}

	@Override
	public boolean neat$isIdBlacklisted() {
		return neat$isIdBlacklisted;
	}

	@Override
	public void neat$setIdBlacklisted(boolean value) {
		this.neat$isIdBlacklisted = value;
	}

	@Override
	public boolean neat$shouldShowPlate() {
		return neat$shouldShowPlate;
	}

	@Override
	public void neat$setShowPlate(boolean value) {
		this.neat$shouldShowPlate = value;
	}

	@Override
	public FormattedCharSequence neat$nameToRender() {
		return this.neat$nameToRender;
	}

	@Override
	public void neat$setNameToRender(FormattedCharSequence value) {
		this.neat$nameToRender = value;
	}

	@Override
	public float neat$getHealth() {
		return this.neat$health;
	}

	@Override
	public void neat$setHealth(float value) {
		this.neat$health = value;
	}

	@Override
	public float neat$getMaxHealth() {
		return this.neat$maxHealth;
	}

	@Override
	public void neat$setMaxHealth(float value) {
		this.neat$maxHealth = value;
	}

	@Override
	public NeatTypeIcon neat$getTypeIcon() {
		return neat$typeIcon;
	}

	@Override
	public void neat$setTypeIcon(NeatTypeIcon value) {
		this.neat$typeIcon = value;
	}

	@Override
	public int neat$getArmorValue() {
		return this.neat$armorValue;
	}

	@Override
	public void neat$setArmorValue(int value) {
		this.neat$armorValue = value;
	}
}
