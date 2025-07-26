package net.mcreator.madokraftmagica.gems.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.madokraftmagica.MadokraftmagicaMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GemTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MadokraftmagicaMod.MODID);

    public static final RegistryObject<CreativeModeTab> SOUL_GEMS = REGISTRY.register("soul_gems",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.madokraftmagica.soul_gems"))
                    .icon(() -> new ItemStack(GemItems.PINK_SOUL_GEM.get()))
                    .displayItems((parameters, tabData) -> {
                        tabData.accept(GemItems.SOUL_GEM.get());
                        tabData.accept(GemItems.RED_SOUL_GEM.get());
                        tabData.accept(GemItems.PINK_SOUL_GEM.get());
                        tabData.accept(GemItems.BLUE_SOUL_GEM.get());
                        tabData.accept(GemItems.YELLOW_SOUL_GEM.get());
                        tabData.accept(GemItems.GREEN_SOUL_GEM.get());
                        tabData.accept(GemItems.PURPLE_SOUL_GEM.get());
                    })
                    .build());
}
