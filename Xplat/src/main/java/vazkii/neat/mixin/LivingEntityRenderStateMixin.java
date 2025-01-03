package vazkii.neat.mixin;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import vazkii.neat.NeatRenderState;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements NeatRenderState {
	@Unique
	public boolean neat$isBoss;
	@Unique
	public boolean neat$isFriendly;
	@Unique
	public boolean neat$isIdBlacklisted;

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
}
