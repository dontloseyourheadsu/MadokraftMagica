package net.mcreator.madokraftmagica.gems.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

import net.mcreator.madokraftmagica.MadokraftmagicaMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GemTabs {
        public static final CreativeModeTab SOUL_GEMS = new CreativeModeTab("madokraftmagica.soul_gems") {
                @Override
                public ItemStack makeIcon() {
                        return new ItemStack(GemItems.PINK_SOUL_GEM.get());
                }
        };
}
