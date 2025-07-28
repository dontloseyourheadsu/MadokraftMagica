package net.mcreator.madokraftmagica.kyubey.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class KyubeyContractMenuProvider implements MenuProvider {
    private final KyubeyEntity kyubey;

    public KyubeyContractMenuProvider(KyubeyEntity kyubey) {
        this.kyubey = kyubey;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Wanna make a contract?");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory,
            @Nonnull Player player) {
        return new KyubeyContractMenu(containerId, playerInventory, kyubey);
    }
}
