package vazkii.neat;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface NeatRenderState {
	boolean neat$isBoss();
	void neat$setBoss(boolean value);
	boolean neat$isFriendly();
	void neat$setFriendly(boolean value);
	boolean neat$isIdBlacklisted();
	void neat$setIdBlacklisted(boolean value);
	boolean neat$shouldShowPlate();
	void neat$setShowPlate(boolean value);
	FormattedCharSequence neat$nameToRender();
	void neat$setNameToRender(FormattedCharSequence value);
	float neat$getHealth();
	void neat$setHealth(float value);
	float neat$getMaxHealth();
	void neat$setMaxHealth(float value);
	int neat$getArmorValue();
	void neat$setArmorValue(int value);
	List<ItemStackRenderState> neat$ironArmorIcons();
	void neat$addIronArmorIcons(ItemStackRenderState renderState);
	List<ItemStackRenderState> neat$diamondArmorIcons();
	void neat$addDiamondArmorIcons(ItemStackRenderState renderState);
	ItemStackRenderState neat$getTypeIconState();
	void neat$setTypeIconState(ItemStackRenderState renderState);
}
