package net.mcreator.madokraftmagica.gems.init;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.mcreator.madokraftmagica.MadokraftmagicaMod;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GemEntitySetup {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Empty for now - gems don't have entities, only Kyubey which is in its own
        // package
    }
}
