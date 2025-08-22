package net.mcreator.madokraftmagica.kyubey.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.kyubey.init.KyubeyMenus;
import net.mcreator.madokraftmagica.kyubey.client.screen.KyubeyScreenState;

import javax.annotation.Nonnull;

public class KyubeyContractMenu extends AbstractContainerMenu {
    private final KyubeyEntity kyubey;
    private KyubeyScreenState currentState = KyubeyScreenState.CONTRACT_CONFIRMATION;

    public KyubeyContractMenu(int containerId, Inventory playerInventory, KyubeyEntity kyubey) {
        super(KyubeyMenus.KYUBEY_CONTRACT.get(), containerId);
        this.kyubey = kyubey;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return this.kyubey.isAlive() && this.kyubey.distanceToSqr(player) < 64.0D;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        // End interaction when menu is closed (in case player used ESC or clicked
        // outside)
        if (!this.kyubey.level.isClientSide) {
            this.kyubey.endInteraction();
        }
    }

    public KyubeyEntity getKyubey() {
        return this.kyubey;
    }

    public KyubeyScreenState getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(KyubeyScreenState state) {
        this.currentState = state;
    }
}
