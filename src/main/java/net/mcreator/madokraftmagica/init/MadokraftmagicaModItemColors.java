package net.mcreator.madokraftmagica.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.madokraftmagica.MadokraftmagicaMod;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MadokraftmagicaModItemColors {

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		// Red Soul Gem - Red color (0xFF5555)
		event.register((itemStack, tintIndex) -> tintIndex == 1 ? 0xFF5555 : 0xFFFFFF, 
			MadokraftmagicaModItems.RED_SOUL_GEM.get());
		
		// Pink Soul Gem - Pink color (0xFF55FF)
		event.register((itemStack, tintIndex) -> tintIndex == 1 ? 0xFF55FF : 0xFFFFFF, 
			MadokraftmagicaModItems.PINK_SOUL_GEM.get());
		
		// Blue Soul Gem - Blue color (0x5555FF)
		event.register((itemStack, tintIndex) -> tintIndex == 1 ? 0x5555FF : 0xFFFFFF, 
			MadokraftmagicaModItems.BLUE_SOUL_GEM.get());
		
		// Yellow Soul Gem - Yellow color (0xFFFF55)
		event.register((itemStack, tintIndex) -> tintIndex == 1 ? 0xFFFF55 : 0xFFFFFF, 
			MadokraftmagicaModItems.YELLOW_SOUL_GEM.get());
		
		// Purple Soul Gem - Purple color (0xAA55FF)
		event.register((itemStack, tintIndex) -> tintIndex == 1 ? 0xAA55FF : 0xFFFFFF, 
			MadokraftmagicaModItems.PURPLE_SOUL_GEM.get());
	}
}
