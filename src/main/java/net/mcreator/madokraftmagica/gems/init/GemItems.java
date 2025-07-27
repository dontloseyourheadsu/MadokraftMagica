/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.madokraftmagica.gems.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.gems.item.SoulGemItem;
import net.mcreator.madokraftmagica.gems.item.RedSoulGemItem;
import net.mcreator.madokraftmagica.gems.item.PinkSoulGemItem;
import net.mcreator.madokraftmagica.gems.item.BlueSoulGemItem;
import net.mcreator.madokraftmagica.gems.item.YellowSoulGemItem;
import net.mcreator.madokraftmagica.gems.item.GreenSoulGemItem;
import net.mcreator.madokraftmagica.gems.item.PurpleSoulGemItem;
import net.mcreator.madokraftmagica.kyubey.item.KyubeySpawnEggItem;

public class GemItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            MadokraftmagicaMod.MODID);
    public static final RegistryObject<Item> SOUL_GEM = REGISTRY.register("soul_gem", () -> new SoulGemItem());
    public static final RegistryObject<Item> RED_SOUL_GEM = REGISTRY.register("red_soul_gem",
            () -> new RedSoulGemItem());
    public static final RegistryObject<Item> PINK_SOUL_GEM = REGISTRY.register("pink_soul_gem",
            () -> new PinkSoulGemItem());
    public static final RegistryObject<Item> BLUE_SOUL_GEM = REGISTRY.register("blue_soul_gem",
            () -> new BlueSoulGemItem());
    public static final RegistryObject<Item> YELLOW_SOUL_GEM = REGISTRY.register("yellow_soul_gem",
            () -> new YellowSoulGemItem());
    public static final RegistryObject<Item> GREEN_SOUL_GEM = REGISTRY.register("green_soul_gem",
            () -> new GreenSoulGemItem());
    public static final RegistryObject<Item> PURPLE_SOUL_GEM = REGISTRY.register("purple_soul_gem",
            () -> new PurpleSoulGemItem());
    public static final RegistryObject<Item> KYUBEY_SPAWN_EGG = REGISTRY.register("kyubey_spawn_egg",
            () -> new KyubeySpawnEggItem());
    // Start of user code block custom items
    // End of user code block custom items
}
