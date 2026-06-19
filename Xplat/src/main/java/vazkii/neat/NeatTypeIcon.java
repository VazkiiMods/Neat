package vazkii.neat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public enum NeatTypeIcon {
	NONE(ItemStack.EMPTY),
	BOSS(Items.NETHER_STAR),
	ARTHROPOD(Items.SPIDER_EYE),
	UNDEAD(Items.ROTTEN_FLESH),
	ILLAGER(Items.IRON_AXE);

	final ItemStack item;

	NeatTypeIcon(ItemStack item) {
		this.item = item;
	}

	NeatTypeIcon(ItemLike item) {
		this(new ItemStack(item));
	}

	ItemStack getIcon() {
		return item;
	}
}
