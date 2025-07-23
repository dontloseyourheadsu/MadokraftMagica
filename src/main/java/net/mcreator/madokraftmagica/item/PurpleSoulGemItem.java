package net.mcreator.madokraftmagica.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

public class PurpleSoulGemItem extends Item {
	public PurpleSoulGemItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(@Nonnull ItemStack itemstack) {
		return false;
	}
}
