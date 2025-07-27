package net.mcreator.madokraftmagica.kyubey.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.mcreator.madokraftmagica.kyubey.init.KyubeyEntities;
import net.mcreator.madokraftmagica.gems.init.GemTabs;

public class KyubeySpawnEggItem extends SpawnEggItem {
    public KyubeySpawnEggItem() {
        super(KyubeyEntities.KYUBEY.get(), 0xFFFFFF, 0xFF0000, new Item.Properties().tab(GemTabs.SOUL_GEMS).stacksTo(64));
    }
}
