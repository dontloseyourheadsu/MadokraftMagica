package net.mcreator.madokraftmagica.kyubey.init;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.kyubey.entity.client.KyubeyRenderer;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KyubeyEntitySetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(KyubeyEntities.KYUBEY.get(), KyubeyRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(KyubeyEntities.KYUBEY.get(), KyubeyEntity.createAttributes().build());
    }
}
