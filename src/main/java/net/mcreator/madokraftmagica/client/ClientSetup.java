package net.mcreator.madokraftmagica.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.screens.MenuScreens;

import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.init.KyubeyMenus;
import net.mcreator.madokraftmagica.kyubey.client.screen.KyubeyContractScreen;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        // Client setup for items - models should be automatically loaded from the
        // correct paths
        // No additional registration needed for item models in 1.19.2 if they're in the
        // correct location

        // Register Kyubey contract screen
        event.enqueueWork(() -> {
            MenuScreens.register(KyubeyMenus.KYUBEY_CONTRACT.get(), KyubeyContractScreen::new);
        });
    }
}
