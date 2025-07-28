package net.mcreator.madokraftmagica.gems.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.mcreator.madokraftmagica.kyubey.init.KyubeyEntities;

public class KyubeySpawnEggItem extends SpawnEggItem {
    public KyubeySpawnEggItem() {
        super(KyubeyEntities.KYUBEY.get(), 0xFFFFFF, 0xFF0000, new Item.Properties().stacksTo(64));
    }
}