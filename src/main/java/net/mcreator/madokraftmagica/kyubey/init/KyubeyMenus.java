package net.mcreator.madokraftmagica.kyubey.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.menu.KyubeyContractMenu;

public class KyubeyMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            MadokraftmagicaMod.MODID);

    public static final RegistryObject<MenuType<KyubeyContractMenu>> KYUBEY_CONTRACT = REGISTRY.register(
            "kyubey_contract",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                int entityId = data.readInt();
                if (inv.player.level.getEntity(
                        entityId) instanceof net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity kyubey) {
                    return new KyubeyContractMenu(windowId, inv, kyubey);
                }
                return null;
            }));
}
