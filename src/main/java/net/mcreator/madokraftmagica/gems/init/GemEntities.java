package net.mcreator.madokraftmagica.gems.init;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;

public class GemEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            MadokraftmagicaMod.MODID);

    // Empty for now - gems don't have entities, only Kyubey which is in its own
    // package
}
