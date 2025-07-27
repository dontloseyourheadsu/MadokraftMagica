package net.mcreator.madokraftmagica.kyubey.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;

public class KyubeyEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            MadokraftmagicaMod.MODID);

    public static final RegistryObject<EntityType<KyubeyEntity>> KYUBEY = REGISTRY.register("kyubey",
            () -> EntityType.Builder.of(KyubeyEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 0.8f)
                    .build("kyubey"));
}
